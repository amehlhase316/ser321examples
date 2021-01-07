import java.net.*;
import java.util.Arrays;

/**
 * A class to demonstrate Java's support for internet addresses.
 * 
 * @author Tim Lindquist Tim.Lindquist@asu.edu Software Engineering, CIDSE,
 *         IAFSE, ASU Poly
 * @version April 2020
 * 
 * @modified-by David Clements dacleme1@asu.edu August 2020
 * 
 */

public class GetIP {

  public static void main(String args[]) {
    if(args.length != 1)
	    System.out.println(
	       "Expected Arguments: <url(String)>");
    else
      try {
        // convert hostname argument to IP address
        String hostNameArg = args[0];
        InetAddress address = InetAddress.getByName(hostNameArg);

        // string string output and print
        String hostAddress = address.getHostAddress();
        System.out.println(hostAddress);

        // get ip byte representation and print
        byte ipByes[] = address.getAddress();
        System.out.println(Arrays.toString(ipByes));

      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
  }
}
