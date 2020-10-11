import java.net.*;
import java.io.*;
import java.util.*;

/**
 * A class for simple client-server connections with a threaded server. This
 * example uses Object Input and Output streams to communicate between client
 * and server.
 *
 * Ser321 Foundations of Distributed Software Systems see
 * http://pooh.poly.asu.edu/Ser321
 * 
 * @author Tim Lindquist Tim.Lindquist@asu.edu Software Engineering, CIDSE,
 *         IAFSE, ASU Poly
 * @version April 2020
 */
class ThreadedSockClient {
  public static void main(String args[]) throws IOException {
    if (args.length != 2) {
      System.out.println("Usage: gradle ThreadedSockClient -Phost=localhost -Pport=8888 -q --console=plain");
      System.exit(0);
    }
    String host = args[0];
    int portNo = Integer.parseInt(args[1]);
    Socket sock = null; // needed
    try {
      // open socket
      sock = new Socket(host, portNo);
      // get raw socket
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
      // declare the object streams using the raw socket
      ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
      ObjectInputStream is = new ObjectInputStream(sock.getInputStream());

      // take input from the user
      System.out.print("Line number to get [0-4, empty to exit]>");
      // break apart the string into tokens(space delimited, by default)
      StringTokenizer inTokens = new StringTokenizer(stdin.readLine());

      // pull first token, if there is one
      String inStr = "";
      if (inTokens.hasMoreTokens()) {
        inStr = inTokens.nextToken();
      }

      // process loop to read and echo every word sent from the server
      while (true) {
        // send the token(will be verified for being a single digit(number)
        os.writeObject(inStr);
        // exit process loop if token was "end"
        if (inStr.equals("end")) {
          break;
        }
        // wait for response and print it
        System.out.println((String) is.readObject());
        // get more tokens
        System.out.print("get>");
        inTokens = new StringTokenizer(stdin.readLine());
        // pull first token, if available. exit if input was empty
        if (inTokens.hasMoreTokens()) {
          inStr = inTokens.nextToken();
        } else {
          inStr = "end"; // will exit after it is sent above.
        }
      }
      sock.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // clean up sockets when done, if instanced
      if (sock != null)
        sock.close();
    }
  }
}
