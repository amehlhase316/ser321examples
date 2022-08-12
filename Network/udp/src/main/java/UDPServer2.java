import java.net.*;
import java.io.*;

public class UDPServer2 {
	public static void main(String args[]){ 
		DatagramSocket aSocket = null;
        if (args.length != 1) {
          System.out.println("Expected Arguments: <port(int)>");
          System.exit(0);
        }
        int portNo = 9099; // default port
        try {
            portNo = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("port must be integer");
            System.exit(2);
        }
		try{
			aSocket = new DatagramSocket(portNo);
			// create socket at agreed port
			byte[] buffer = new byte[1000];
			while(true){
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);     
				System.out.println("RECEIVED: " + new String(request.getData()));
				// construct a reply packet from request
				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
						request.getAddress(), request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) { 
			System.out.println("IO: " + e.getMessage());
		} finally { 
			if(aSocket != null) 
				aSocket.close();
			}
	}
}
