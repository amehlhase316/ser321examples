import java.net.*;
import java.io.*;
public class MulticastPeer{
    public static void main(String args[]){ 
		// args give message contents and destination multicast group (e.g. "228.5.6.7")
		MulticastSocket s =null;
        int port = -1;
        if (args.length != 3) {
          System.out.println("USAGE: java MulticastPeer message<String> address<String> port<int>");
          System.exit(1);
        }
        try {
          port = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            System.out.println("port must be integers");
            System.exit(2);
        }
		try {
			InetAddress group = InetAddress.getByName(args[1]);
			s = new MulticastSocket(port);
			s.joinGroup(group);
 			byte [] m = args[0].getBytes();
			DatagramPacket messageOut = new DatagramPacket(m, m.length, group, port);
			s.send(messageOut);	
 			for(int i = 0; i < 3;i++) {		// get messages from others in group
                byte[] buffer = new byte[10 * 1024];
 				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 				s.receive(messageIn);
 				System.out.println("Received:" + new String(messageIn.getData()));
  			}
			s.leaveGroup(group);		
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(s != null) s.close();}
	}		      	
	
}
