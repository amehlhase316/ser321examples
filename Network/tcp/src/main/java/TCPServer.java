import java.net.*;
import java.io.*;
/**
 * A server class for client-server connections using a TCP connection.
 */
public class TCPServer {
	public static void main (String args[]) {
		ServerSocket listenSocket = null;
        if (args.length != 2) {
          System.out.println("Wrong number of arguments:\ngradle runServer --args=\"8888 9\"");
          System.exit(0);
        }
        int portNo = 9099; // default port
        int delay = 9; // default delay
        try {
            portNo = Integer.parseInt(args[0]);
            delay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("port must be integer");
            System.exit(2);
        }
		try {
			listenSocket = new ServerSocket(portNo); // initialize a server socket listening for connections from portNo
			while(true) { // Loop for server socket to accept new connections as they are received
				Socket clientSocket = listenSocket.accept();
				new Connection(clientSocket, delay);
			}
		} catch(IOException e) {
			System.out.println("Listen socket:"+e.getMessage());
		} finally {
			if (listenSocket != null && listenSocket.isClosed()) {
				try {
					listenSocket.close();
				} catch (Throwable t) {
					System.out.println("Problem closing ServerSocket " + t.getMessage());
				}
			}
		}
	}
}

/**
 * Connection class for TCP server that extends Thread class
 * this is used to create a thread for a client connection and create a DataInputStream and DataOutputStream
 * to handle communication between the two.
 */
class Connection extends Thread {
	DataInputStream in; // data input stream lets the application read primitive Java data types.
	DataOutputStream out; //data output stream lets the application write data that can be read by a data input stream.
	Socket clientSocket;
	long __msDelay;

	public Connection (Socket aClientSocket, long msDelay) {
		try {
			clientSocket = aClientSocket;
			in = new DataInputStream( clientSocket.getInputStream());
			out = new DataOutputStream( clientSocket.getOutputStream());
			__msDelay = msDelay;
			this.start();
		} catch(IOException e) {
			System.out.println("Connection:"+e.getMessage());
		}
	}

	/**
	 * Run method for Connection thread.
	 */
	public void run(){
		try {			                 // an echo server
			String data = in.readUTF();	 // read a line of data from the stream
			System.out.println("Read " + data);
			Thread.sleep(__msDelay); // suspends current thread for specified delay
			out.writeUTF(data); // writes data to output stream using modified UTF-8 encoding
		} catch (EOFException e) {
			System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {
			System.out.println("readline:"+e.getMessage());
		} catch (Throwable t) {
			System.out.println("Caught some other ugliness " + t.getMessage());
		}
		finally { 
			try {
				clientSocket.close(); // closes client socket
			} catch (IOException e) {
				/*close failed*/
			}
		}	
	}
}
