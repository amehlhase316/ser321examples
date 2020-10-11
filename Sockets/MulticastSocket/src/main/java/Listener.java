import java.net.*;
import java.io.*;

class Listener {

	public static void main(String[] args) throws Exception {
        DatagramPacket packet;
        int port = -1;
        if (args.length != 2) {
          System.out.println("USAGE: java Listener address<<228.5.6.7>> port<<2222>>");
          System.exit(1);
        }
        try {
          port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("port must be integers");
            System.exit(2);
        }
        String addr = args[0];
		InetAddress group = InetAddress.getByName(addr);
		MulticastSocket socket = new MulticastSocket(port);
		socket.joinGroup(group);

		String msgS;
        byte[] msg = new byte[10*1024];
        packet = new DatagramPacket(msg, msg.length);
		do {
            System.out.println("Waiting for a multicast message");
			socket.receive(packet);
			msgS = new String(msg, 0, packet.getLength());
			System.out.println("Message: *" + msgS + "*");
		} while (!msgS.equals("stop"));

		socket.leaveGroup(group);
        socket.close();
	}
}
