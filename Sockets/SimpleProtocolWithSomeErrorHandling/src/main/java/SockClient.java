import org.json.JSONArray;
import org.json.JSONObject;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 * 
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 */
class SockClient {
  public static void main (String args[]) {
    Socket sock = null;
    String host = "localhost";
    
    try {
      // open the connection
      sock = new Socket(host, 8888); // connect to host and socket on port 8888

      // get output channel
      OutputStream out = sock.getOutputStream();

      // create an object output writer (Java only)
      ObjectOutputStream os = new ObjectOutputStream(out);

      DataInputStream in = new DataInputStream(sock.getInputStream());

      Scanner scanner = new Scanner(System.in);
      String message = scanner.nextLine();
// I am lazy here and just hard coding the different requests here


      JSONObject json = new JSONObject();
//      json.put("type", "echo");
//      json.put("data", message);

//      json.put("type", "add");
//      json.put("num1", 454);
//      json.put("num2", 2);

      JSONArray array = new JSONArray();
      array.put(1);
      array.put(2);
      array.put(5);

      json.put("type", "addmany");
      json.put("nums", array);

      // write the whole message
      os.writeObject(json.toString());

      // make sure it wrote and doesn't get cached in a buffer
      os.flush();

      String i = (String) in.readUTF();
//      System.out.println(i);
      JSONObject res = new JSONObject(i);

      if (res.getBoolean("ok")){
        if (res.getString("type").equals("echo")) {
          System.out.println(res.getString("result"));
        } else {
          System.out.println(res.getInt("result"));
        }
      } else {
        System.out.println(res.getString("message"));
      }

      //closing things
      in.close();
      os.close();
      sock.close(); // close socked after sending

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}