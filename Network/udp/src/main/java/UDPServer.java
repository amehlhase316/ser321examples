import java.io.*;
import java.net.*;

class UDPServer
{
	public static void main(String args[]) throws Exception
	{
        if (args.length != 2) {
          System.out.println("gradle runServer --args=\"8888 5\"");
          System.exit(0);
        }
        int portNo = 9099; // default port
        int delay = 9; // default port
        try {
            portNo = Integer.parseInt(args[0]);
            delay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("port must be integer");
            System.exit(2);
        }
		DatagramSocket serverSocket = new DatagramSocket(portNo);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while(true)
		{
			// receive request
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String( receivePacket.getData());
			System.out.println("RECEIVED: " + sentence);
			if (args.length > 0) Thread.sleep(delay);
			// pull reply address
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();
			// send response
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket =
					new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
		}
	}
}
