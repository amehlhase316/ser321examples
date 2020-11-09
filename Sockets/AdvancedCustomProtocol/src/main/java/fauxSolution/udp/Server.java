package fauxSolution.udp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

import org.json.*;

public class Server {
  /*
   * request: { "selected": <int: 1=joke, 2=quote, 3=image, 4=random>,
   * (optional)"min": <int>, (optional)"max":<int> }
   * 
   * response: {"datatype": <int: 1-string, 2-byte array>, "type": <"joke",
   * "quote", "image"> "data": <thing to return> }
   * 
   * error response: {"error": <error string> }
   */

  public static JSONObject joke() {
    JSONObject json = new JSONObject();
    json.put("datatype", 1);
    json.put("type", "joke");
    json.put("data", "What does a baby computer call its father? Data.");
    return json;
  }

  public static JSONObject quote() {
    JSONObject json = new JSONObject();
    json.put("datatype", 1);
    json.put("type", "quote");
    json.put("data",
        "A good programmer is someone who always looks both ways before crossing a one-way street. (Doug Linder)");
    return json;
  }

  public static JSONObject image() throws IOException {
    JSONObject json = new JSONObject();
    json.put("datatype", 2);

    json.put("type", "image");

    File file = new File("img/To-Funny-For-Words1.png");
    if (!file.exists()) {
      System.err.println("Cannot find file: " + file.getAbsolutePath());
      System.exit(-1);
    }
    // Read in image
    BufferedImage img = ImageIO.read(file);
    byte[] bytes = null;
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ImageIO.write(img, "png", out);
      bytes = out.toByteArray();
    }
    if (bytes != null) {
      Base64.Encoder encoder = Base64.getEncoder();
      json.put("data", encoder.encodeToString(bytes));
      return json;
    }
    return error("Unable to save image to byte array");
  }

  public static JSONObject random() throws IOException {
    Random rand = new Random();
    int random = rand.nextInt(3);
    JSONObject json = new JSONObject();
    if (random == 0) {
      json = joke();
    } else if (random == 1) {
      json = quote();
    } else if (random == 2) {
      json = image();
    }
    return json;
  }

  public static JSONObject error(String err) {
    JSONObject json = new JSONObject();
    json.put("error", err);
    return json;
  }

  public static void main(String[] args) throws IOException {
    DatagramSocket sock = null;
    try {
      sock = new DatagramSocket(9000);
      // NOTE: SINGLE-THREADED, only one connection at a time
      while (true) {
        try {
          while (true) {
            NetworkUtils.Tuple messageTuple = NetworkUtils.Receive(sock);
            JSONObject message = JsonUtils.fromByteArray(messageTuple.Payload);
            JSONObject returnMessage;
            if (message.has("selected")) {
              if (message.get("selected") instanceof Long || message.get("selected") instanceof Integer) {
                int choice = message.getInt("selected");
                switch (choice) {
                case (1):
                  returnMessage = joke();
                  break;
                case (2):
                  returnMessage = quote();
                  break;
                case (3):
                  returnMessage = image();
                  break;
                case (4):
                  returnMessage = random();
                  break;
                default:
                  returnMessage = error("Invalid selection: " + choice + " is not an option");
                }
              } else {
                returnMessage = error("Selection must be an integer");
              }
            } else {
              returnMessage = error("Invalid message received");
            }

            byte[] output = JsonUtils.toByteArray(returnMessage);
            NetworkUtils.Send(sock, messageTuple.Address, messageTuple.Port, output);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (sock != null) {
        sock.close();
      }
    }
  }
}