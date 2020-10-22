import java.net.*;
import java.io.*;

/**
 * Client class to demonstrate a simple "echo" client-server connection using sockets.
 * @version October 2020
 *
 */
public class Client {
public static void main (String args[]) {
        try {
                if (args.length != 2) {
                        System.out.println("Usage: gradle ThreadedSockClient -Pport=9099 -Phost=localhost");
                        System.exit(0);
                }
                int port = -1;
                try {
                        port = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                        System.out.println("[Port] must be an integer");
                        System.exit(2);
                }
                String host = args[0];
                Socket server = new Socket(host, port);
                System.out.println("Connected to server at " + host + ":" + port);
                InputStream input = server.getInputStream();
                OutputStream output = server.getOutputStream();
                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

                int bufLen = 1024;
                byte bytesReceived[] = new byte[bufLen];
                while(true) {
                        /* read from the command line */ 
                        System.out.print("String to send> ");
                        String strToSend = stdin.readLine();
                        if (strToSend.equals("quit")) {
                                break;
                        }

                        /* send to server */
                        byte bytesToSend[] = strToSend.getBytes();
                        output.write(bytesToSend,0,bytesToSend.length);
                        System.out.println("String to send:" + strToSend);
                        
                        /* read from server */
                        int numBytesReceived = input.read(bytesReceived, 0, bufLen);
                        String strReceived = new String(bytesReceived,0,numBytesReceived);
                        System.out.println("Received from server: " + strReceived);
                }
                server.close();
        } catch(Exception e) {
                e.printStackTrace();
        }
}
}
