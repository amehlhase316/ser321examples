import java.net.*;
import java.io.*;

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
                // read in one object, the message. we know a string was written only by knowing what the client sent. 
                // must cast the object from Object to desired type to be useful
                receviedString = (String) in.readObject();
                System.out.println("Received the String "+receviedString);
                // read in the number, we know it's an integer because that's the second thing sent by the client.
                receivedInt = (Integer) in.readObject();
                System.out.println("Received the Integer "+ receivedInt);

            }

            //if ouput stream is initialized write output
            if (out != null && os != null){

                //if the string received is exit or integer received is 0 exit
                if(receviedString.equalsIgnoreCase("exit") || receivedInt == 0){
                
                  os.writeObject("exiting");
                  //close the streams and socket server
                os.close();
                in.close();
                sock.close();
                connected = false;
                
                }else{
                  os.writeObject(receivedInt + " and " + receviedString + " ... Got it!");
                }
            }
        }  
      } catch(Exception e) {
        e.printStackTrace();
        
      }
  }
}
