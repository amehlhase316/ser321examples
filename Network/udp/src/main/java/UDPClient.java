import java.io.*;
import java.net.*;

class UDPClient
{
	public static void main(String args[]) throws Exception
	{
        if (args.length != 2) {
          System.out.println("Expected Arguments: <host(String)> <port(int)>");
          System.exit(0);
        }
        String host = args[0];
        int portNo = 9099; // default port
        try {
            portNo = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("port must be integer");
            System.exit(2);
        }
		System.out.println("Type your message:");

		// setup packet
        BufferedReader inFromUser =
				new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(host);
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		String sentence = inFromUser.readLine();
		sendData = sentence.getBytes();
		
		//send packet
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portNo);
		clientSocket.send(sendPacket);
		// receive packet
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		// output
		String modifiedSentence = new String(receivePacket.getData());
		System.out.println("FROM SERVER:" + modifiedSentence);
		clientSocket.close();
    }
}
