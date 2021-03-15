import java.net.*;
import java.io.*;

class Sender {

	public static void main(String[] args) throws Exception {
        int port = -1;
        if (args.length != 3) {
          System.out.println("USAGE: java Sender message<<hello>> address<<228.5.6.7>> port<<2222>>");
          System.exit(1);
        }
        try {
          port = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            System.out.println("port must be integers");
            System.exit(2);
        }

		String msg = args[0];
    String addr = args[1];
		InetAddress group = InetAddress.getByName(addr);
    MulticastSocket socket = new MulticastSocket(port);
    socket.joinGroup(group);
		DatagramPacket packet =
			new DatagramPacket(msg.getBytes(), msg.length(), group, port);
      socket.send(packet);
      socket.leaveGroup(group);
      socket.close();
	}
}
