
import java.net.*;
import java.util.*;

/**
 * A class to demonstrate receiving a datagram packet.
 * @version April 2020
 * 
 * @modified-by David Clements dacleme1@asu.edu August 2020
 *
 */
public class GetNameForIP {

  public static void main(String args[]) {
    if(args.length != 1)
	    System.out.println(
	       "usage: java ser321.sockets.GetNameForIP 8.8.4.4");
    else
      try {
        // IPv4 addresses are 32-bit in the format "byte . byte . byte . byte"
        // each byte is known as an octet. there are 4 octets per IPv4 address.
        String delimiter = "."; // ip bytes are separated by periods
        
        // tokenize(split up) the ip address string into the octets
        StringTokenizer st = new StringTokenizer(args[0], delimiter);

        // * <---- remove a slash to toggle which code block below is active

        // Method 1: Manual conversion of IP String to bytes (Hard Mode)

        // storage space for each byte
        byte ip[] = new byte[4];
        int i = 0; // set up array offset to store byte in to
        while (st.hasMoreElements()) { // make sure there is a token to parse
          String seg = (String) st.nextElement(); // pull number string
          int next = Integer.parseInt(seg); // parse number

          // bit-manipulation ahead
          // due to a restriction with number representation in Java
          // bytes are converted to signed representation.
          // bytes are unsigned 8-bit integers
          // a byte's range is -128 to 127 in signed world, 256/2 on either side of 0
          // because one bit goes to sign and zero is significant.
          // therefore when we store 'next', it can only have a max value of 127 and we
          // need to check for it.
          if (next > 127) {
            // if it's greater, we need to normalize the bit-representation of it by
            // subtracting 256.
            next = next - 256;
          }

          // store and move to next byte
          ip[i] = (new Integer(next)).byteValue();
          i++;
        }

        /*
         * /
         * 
         * //Method 2: Use the built-in utilities (Easy Mode)
         * 
         * InetAddress address = InetAddress.getByName(args[0]); byte[] ip =
         * address.getAddress();
         * 
         * //
         */

        // convert IP to Hostname
        String hostName = (InetAddress.getByAddress(ip)).getHostName();

        // output
        System.out.println("Domain name for IP: " + args[0] + " is: " + hostName);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
  }
}
