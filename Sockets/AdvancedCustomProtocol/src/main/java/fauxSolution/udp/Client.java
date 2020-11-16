package fauxSolution.udp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.*;

public class Client {
	  /*
	   * request: { "selected": <int: 1=joke, 2=quote, 3=image, 4=random>,
	   * (optional)"min": <int>, (optional)"max":<int> }
	   * 
	   * response: {"datatype": <int: 1-string, 2-byte array>, "type": <"joke", "quote", "image"> "data": <thing to
	   * return> }
	   * 
	   * error response: {"error": <error string> }
	   */
public static JSONObject joke() {
  JSONObject request = new JSONObject();
  request.put("selected", 1);
  return request;
}

public static JSONObject quote() {
  JSONObject request = new JSONObject();
  request.put("selected", 2);
  return request;
}

public static JSONObject image() {
  JSONObject request = new JSONObject();
  request.put("selected", 3);
  return request;
}

public static JSONObject random() {
  JSONObject request = new JSONObject();
  request.put("selected", 4);
  return request;
}

  public static void main(String[] args) throws IOException {
    DatagramSocket sock;
    try {
      InetAddress address = InetAddress.getByName("localhost");
      int port = 9000;
      sock = new DatagramSocket();

      Scanner input = new Scanner(System.in);
      int choice;
      System.out.println("Please select a valid option (1-5). 0 to diconnect the client");
      do {
        choice = input.nextInt(); // what if not int? .. should error handle this
        JSONObject request = null;
        switch (choice) {
        case (1):
          request = joke();
          break;
        case (2):
          request = quote();
          break;
        case (3):
          request = image();
          break;
        case (4):
          request = random();
          break;
        case (5):
        	  System.out.println("Jokes on you, I decided I do not like num 5: https://gph.is/g/a99OP09");
          break;
        default:
          System.out.println("Please select a valid option (1-5).");
          break;
        }

        if (request != null) {
          NetworkUtils.Send(sock, address, port, JsonUtils.toByteArray(request));
          NetworkUtils.Tuple responseTuple = NetworkUtils.Receive(sock);
          JSONObject response = JsonUtils.fromByteArray(responseTuple.Payload);
          if (response.has("error")) {
              System.out.println(response.getString("error"));
            } else {
              switch (response.getInt("datatype")) {
              case (1):
              	  System.out.println("Your " + response.getString("type"));
                System.out.println(response.getString("data"));
                break;
              case (2): {
              	  System.out.println("Your image");
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] bytes = decoder.decode(response.getString("data"));
                ImageIcon icon = null;
                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                  BufferedImage image = ImageIO.read(bais);
                  icon = new ImageIcon(image);
                }
                if (icon != null) {
                  JFrame frame = new JFrame();
                  JLabel label = new JLabel();
                  label.setIcon(icon);
                  frame.add(label);
                  frame.setSize(icon.getIconWidth(), icon.getIconHeight());
                  frame.show();
                }
              }
              break;
            }
          }
        }
      } while (true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}