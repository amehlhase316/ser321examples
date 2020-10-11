import java.net.*;
import java.io.*;

class SockClient {
     public static void main (String args[]) throws Exception {
        Socket          sock = null;
        OutputStream    out = null;
        InputStream     in = null;
        int i1=0, i2=0;
        int port = 9099; // default port

        if (args.length != 4) {
            System.out.println("Expected arguments: <host(String)> <port(int)> <value1(int)> <value2(int)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
            i1 = Integer.parseInt(args[2]);
            i2 = Integer.parseInt(args[3]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port|value1|value2] must be integers");
            System.exit(2);
        }
        try {
            sock = new Socket(host, port);
            out = sock.getOutputStream();
            in = sock.getInputStream();

            out.write(i1);
            out.write(i2);
            int result = in.read();
            System.out.println("Result is " + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)  out.close();
            if (in != null)   in.close();
            if (sock != null) sock.close();
        }
    }
}
