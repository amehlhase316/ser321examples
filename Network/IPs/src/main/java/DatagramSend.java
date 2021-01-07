import java.net.*;
import java.io.*;

/**
 * A class to demonstrate a sending a datagram packet. Ser321 Foundations of
 * Distributed Software Systems see http://pooh.poly.asu.edu/Ser321
 * 
 * @author Tim Lindquist Tim.Lindquist@asu.edu Software Engineering, CIDSE,
 *         IAFSE, ASU Poly
 * @version April 2020
 * 
 * @modified-by David Clements dacleme1@asu.edu August 2020
 * 
 */

class DatagramSend {
  public static void main(String args[]) {
    try {
      // fetch message from arguments
      if (args.length != 2) {
        System.out.println("Expected Arguments: <port(int)> <message(String)>");
      }
      String message = args[1];
      // construct packet
      /// allocate send buffer and assign the bytes from the message
      byte buffer[] = message.getBytes();

      /// create UDP socket
      DatagramSocket sock = new DatagramSocket();

      /// construct the UDP packet using the buffer and fill in the IP headers for
      /// //localhost:8889
      InetAddress receiverIp = InetAddress.getByName("localhost");
      int receiverPort = 9099; // default port
      try {
        receiverPort = Integer.parseInt(args[0]);
      } catch (NumberFormatException nfe) {
          System.out.println("port must be integer");
          System.exit(2);
      }
      DatagramPacket pack = new DatagramPacket(buffer, buffer.length, receiverIp, receiverPort);

      // send packet
      System.out.println("Sending " + message);
      sock.send(pack); // send message and move on

      // NOTE: other actions can be done with this connection. sending more packets or
      // receiving responses can be done.
      // sock.recv() is covered in the DatagramReceive.java.
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

      // clean up connection
      sock.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
