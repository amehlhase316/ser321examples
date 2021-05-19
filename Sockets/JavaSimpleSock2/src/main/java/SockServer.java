import java.net.*;
import java.io.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version August 2020
 * 
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 */
public class SockServer {
  public static void main (String args[]) {
    Socket sock;
    try {
      //open socket
      ServerSocket serv = new ServerSocket(8888); // create server socket on port 8888
      System.out.println("Server ready for 3 connections");
      // only does three connections then closes
      // NOTE: SINGLE-THREADED, only one connection at a time
      for (int rep = 0; rep < 3; rep++){
        System.out.println("Server waiting for a connection");
        sock = serv.accept(); // blocking wait
        // setup the object reading channel
        ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
        
        // read in one object, the message. we know a string was written only by knowing what the client sent. 
        // must cast the object from Object to desired type to be useful
        String s = (String) in.readObject();
        System.out.println("Received the String "+s);
        // read in the number, we know it's an integer because that's the second thing sent by the client.
        Integer i = (Integer) in.readObject();
        System.out.println("Received the Integer "+ i);

        // generate an output
        // get output channel
        OutputStream out = sock.getOutputStream();
        // create an object output writer (Java only)
        ObjectOutputStream os = new ObjectOutputStream(out);
        // write the whole message
        os.writeObject("Got it!");
        // make sure it wrote and doesn't get cached in a buffer
        os.flush();
      }
    } catch(Exception e) {e.printStackTrace();}
  }
}