import java.net.*;
import java.io.*;

/**
 * A client class for client-server connections using a TCP connection.
 */
public class TCPClient {
	public static void main (String args[]) {
		// arguments supply message and hostname
		Socket s = null;
        if (args.length != 3) {
          System.out.println("Wrong number of arguments:\ngradle runClient --args=\"localhost port message\"");
          System.exit(0);
        }
        String host = args[0];
        String message = args[2];
        int portNo = 9099; // default port
        try {
            portNo = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("port must be integer");
            System.exit(2);
        }
		try{
			s = new Socket(host, portNo); //initialize socket with host and portNo
			// Initialize a data input stream, this lets the application read primitive Java data types.
			DataInputStream in = new DataInputStream( s.getInputStream());
			// Initialize a data output stream this lets the application write data
			// that can later be read by a data input stream.
			DataOutputStream out =new DataOutputStream( s.getOutputStream());
			out.writeUTF(message);      	// UTF is a string encoding 
			String data = in.readUTF();	    // read a line of data from the stream
			System.out.println("Received: "+ data) ; 
		} catch (UnknownHostException e) {
			System.out.println("Socket:"+e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:"+e.getMessage());
		} catch (IOException e) { 
			System.out.println("readline:"+e.getMessage());
		} finally {
			if(s!=null) 
				try {
					s.close();
				} catch (IOException e) {
					System.out.println("close:"+e.getMessage());
				}
			}
	}
}
