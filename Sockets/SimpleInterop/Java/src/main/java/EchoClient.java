import java.net.*;
import java.io.*;
import java.util.*;

/**
 * A class for simple client-server connections with a threaded echo server.
 * EchoClient iteratively reads a line of input from the console. It
 * sends the string to the echo server and receives what the server sends
 * back in response. Echo server echo's the string received by the server
 * back to the client.
 *
 * Ser321 Foundations of Distributed Software Systems
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version March 2020
 */
class EchoClient {
   public static int bufLen = 1024;
   
   public static void main (String args[]) {
      Socket sock = null;
      if (args.length != 2) {
         System.out.println("Usage: java ser321.sockets.EchoClient hostName "+
                            "portNumber");
         System.exit(0);
      }
      String host = args[0];
      int portNo = Integer.parseInt(args[1]);
      try {
         sock = new Socket(host, portNo);
         BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
         System.out.print("String to send>");
         String strToSend = stdin.readLine();
         String strReceived;
         OutputStream os = sock.getOutputStream();
         InputStream is = sock.getInputStream();
         int numBytesReceived;
         byte bytesReceived[] = new byte[bufLen];
         while (!strToSend.equalsIgnoreCase("end")){
            byte bytesToSend[] = strToSend.getBytes();
            os.write(bytesToSend,0,bytesToSend.length);
            numBytesReceived = is.read(bytesReceived,0,bufLen);
            strReceived = new String(bytesReceived,0,numBytesReceived);
            System.out.println("Received from server: "+strReceived);
            System.out.print("String to send>");
            strToSend = stdin.readLine();
         }
         sock.close();
      } catch (Exception e) {e.printStackTrace();}
   }
}
