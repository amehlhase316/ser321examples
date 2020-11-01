import java.net.*;
import java.io.*;

public class UDPClient2 {
	public static void main(String args[]){ 
		// args give message contents and destination hostname
		DatagramSocket aSocket = null;
        if (args.length != 3) {
          System.out.println("gradle runClient2 --args=\"localhost port message\"");
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
		try {
			aSocket = new DatagramSocket();    
			InetAddress aHost = InetAddress.getByName(host);
			DatagramPacket request =
					new DatagramPacket(message.getBytes(),  message.length(), aHost, portNo);
			aSocket.send(request);			                        
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
			aSocket.receive(reply);
			System.out.println("Reply: " + new String(reply.getData()));	
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
