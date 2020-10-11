import java.net.*;
import java.io.*;

class SockServer {
    public static void main (String args[]) throws Exception {

        int count = 0;
        ServerSocket    serv = null;
        InputStream in = null;
        OutputStream out = null;
        Socket sock = null;
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        if (args.length != 2) {
          System.out.println("Expected arguments: <port(int)> <delay(int)>");
          System.exit(1);
		}
        
        try {
          port = Integer.parseInt(args[0]);
          sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
          System.out.println("[Port|sleepDelay] must be an integer");
          System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch(Exception e) {
          e.printStackTrace();
          System.exit(2);
        }
        while (serv.isBound() && !serv.isClosed()) {
            System.out.println("Ready...");
            try {
                sock = serv.accept();
                in = sock.getInputStream();
                out = sock.getOutputStream();

                int x = in.read();
                int y = in.read();
                System.out.println("Server received " + x +" "+ y);
                Thread.sleep(sleepDelay);
                out.write(x + y);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null)  out.close();
                if (in != null)   in.close();
                if (sock != null) sock.close();
            }
        }
    }
}

