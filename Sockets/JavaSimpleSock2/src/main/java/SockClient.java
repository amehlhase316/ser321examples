import java.net.*;
import java.io.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
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
    String message = "HI";
    Integer number = 100;

    // works with no inputs or 1, 2 or 3
    // no error handling for wrong arguments
    if (args.length >= 1){ // host, if provided
      host=args[0];
    }
    if (args.length >= 2){
      message = args[1];
    }
    // user provided message and number, ignore 2 arguments 
    if (args.length >= 3){ 
      number = Integer.valueOf(args[2]);
    }
    
    try {
      // open the connection
      sock = new Socket(host, 8888); // connect to host and socket on port 8888
      // get output channel
      OutputStream out = sock.getOutputStream();
      // create an object output writer (Java only)
      ObjectOutputStream os = new ObjectOutputStream(out);
      // write the whole message
      os.writeObject( message);
      os.writeObject( number);
      // make sure it wrote and doesn't get cached in a buffer
      os.flush();

      ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
      String i = (String) in.readObject();
      System.out.println(i);
      sock.close(); // close socked after sending
    } catch (Exception e) {e.printStackTrace();}
  }
}