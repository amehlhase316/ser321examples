import java.net.*;
import java.io.*;

/**
 * 
 * A class to demonstrate receiving a datagram packet. Ser321 Foundations of
 * Distributed Software Systems see http://pooh.poly.asu.edu/Ser321
 * 
 * @author Tim Lindquist Tim.Lindquist@asu.edu Software Engineering, CIDSE,
 *         IAFSE, ASU Poly
 * @version April 2020
 * 
 * @modified-by David Clements dacleme1@asu.edu August 2020
 * 
 */

class DatagramReceive {
  public static void main(String args[]) {
    try {
      if (args.length != 1) {
        System.out.println("Expected Arguments: <port(int)>");
      }
      // allocate receive buffer
      byte buffer[] = new byte[1024];
      // create a packet object to receive UDP packets into
      DatagramPacket pack = new DatagramPacket(buffer, buffer.length);

      // create a socket that can listen in on localhost port 8889
      int portNo = 9099; // default port
      try {
        portNo = Integer.parseInt(args[0]);
      } catch (NumberFormatException nfe) {
          System.out.println("port must be integer");
          System.exit(2);
      }
      DatagramSocket sock = new DatagramSocket(portNo);
      // blocking receive to wait for any communication.
      sock.receive(pack); // this will sit and wait indefinitely

      // sock.setSoTimeout(millis) can be used to have the socket timeout after a time
      // period of milliseconds.
      // this is helpful if you have maintenance tasks. it WILL throw an exception, so
      // be sure to handle them appropriately.

      // NOTE: other actions can be done with this connection. sending more packets or
      // receiving responses can be done.
      // sock.send() is covered in the DatagramSend.java.
      //
      // Active communication with client and server is possible with coordination
      // ==============================
      // A UDP server will have a socket waiting for connection(blocking recv) and
      // respond with a UDP confirmation(send).
      // A UDP client will send a request(send), and then wait for response(recv)
      //
      // NOTE: UDP does not have a standard response. It is best-effort(fire and
      // forget) and WILL NOT give you any
      // reliability unless it is coded into your algorithm.

      // on successful receipt of packet, populate the receive packet object
      buffer = pack.getData();
      // output information
      System.out.println("Received " + new String(pack.getData(), pack.getOffset(), pack.getLength()));

      // clean up connection
      sock.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
