import java.net.*;
import java.io.*;
import org.json.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version February 2021
 * 
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 * @modified-by Kevin Moore <klmoor21@asu.edu> February 2021
 * @modified-by Kevin Moore <a.mehlhase@asu.edu> February 2021
 */
public class SockServer {
  public static void main (String args[]) {
    Socket sock = null;
    Boolean connected = false;
    ObjectInputStream in = null;
    OutputStream out = null;
    ObjectOutputStream os = null;
    String receviedString = "";
    Integer receivedInt = 0;
    try {
        //open socket
        ServerSocket serv = new ServerSocket(8888); // create server socket on port 8888
        System.out.println("Server ready for a connection");
        
        //loop infinitely to get a connection and exchange messages
        while(true){
          
            //if not connected try to connect
            if(!connected){
                System.out.println("Server waiting for a connection");
                sock = serv.accept(); // blocking wait
                // setup the object reading and writing channels
                in = new ObjectInputStream(sock.getInputStream());
                out = sock.getOutputStream();
                os = new ObjectOutputStream(out);
                connected = true;
            }

            //if input stream is initialized, read in object
            if (in != null){

                String jsonRequest = (String) in.readObject();
                System.out.println(jsonRequest);
                JSONObject json = new JSONObject(jsonRequest);
                System.out.println("Server got a type: " + json.getString("type"));
                
                // handling the different request types
                if (json.getString("type").equals("message")){
                    String message = json.getString("value");
                    System.out.println("Message was: " + message);

                    // creating new JSON to send back
                    JSONObject resp = new JSONObject();
                    resp.put("ok", true);
                    resp.put("type", "message");

                    // for reversing our message
                    StringBuilder input = new StringBuilder();
                    input.append(message);
                    resp.put("value", input.reverse());

                    // sending the response
                    os.writeObject(resp.toString());
                } else if (json.getString("type").equals("number")) {
                    int number = json.getInt("value");
                    System.out.println("Number was: " + number);

                    // creating new JSON to send back
                    JSONObject resp = new JSONObject();
                    resp.put("ok", true);
                    resp.put("type", "number");
                    resp.put("value", number * 10); // manipulating input

                    // sending response
                    os.writeObject(resp.toString());
                } else if (json.getString("type").equals("exit")) {
                    // no response we just shut down
                    // you could send a response and then the client would need to catch it (change the breaking of loop)
                    os.close();
                    in.close();
                    sock.close();
                    connected = false;
                }
            }
        }  
      } catch(Exception e) {
        e.printStackTrace();
        
      }
  }
}
