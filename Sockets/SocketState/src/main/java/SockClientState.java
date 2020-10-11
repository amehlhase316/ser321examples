import java.net.*;
import java.io.*;

class SockClientState {
	public static void main (String args[]) throws Exception {
		Socket          sock = null;
		OutputStream    out = null;
		InputStream     in = null;
		int i1=0, i2=0;
        int port = 9099; // default port
		char cmd = ' ';

		if (args.length != 5) {
			System.out.println("Expected arguments: <host(string)> <port(int)> <operation(char)>  <value1(int)> <value2(int)>");
			System.exit(1);
		}

        String host = args[0]; // host
        try {
          port = Integer.parseInt(args[1]);
          i1 = Integer.parseInt(args[3]); // value1
          i2 = Integer.parseInt(args[4]); // value2
        } catch (NumberFormatException nfe) {
          System.out.println("[Port|value1|value2] must be an integer");
          System.exit(2);
        }

		try {
			sock = new Socket(host, port);
			out = sock.getOutputStream();
			in = sock.getInputStream();

			cmd = args[2].charAt(0);
			out.write(cmd);
			switch (cmd) {
			case 'r':
				out.write(i1);
				break;
			default: 
				out.write(i1);
				out.write(i2);
			}
			int result = in.read();
			System.out.println("Result is " + result);
		} catch (NumberFormatException nfe) {
			System.out.println("Command line args must be integers");
			System.exit(2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null)  out.close();
			if (in != null)   in.close();
			if (sock != null) sock.close();
		}
	}
}
