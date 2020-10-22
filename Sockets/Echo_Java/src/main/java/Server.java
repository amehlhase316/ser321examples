import java.net.*;
import java.io.*;

/**
 * Server class to demonstrate a simple "echo" client-server connection using sockets.
 * @version October 2020
 *
 */
public class Server {
public static void main (String args[]) {
        try {
                if (args.length != 1) {
                        System.out.println("Usage: gradle ThreadedSockClient -Pport=9099");
                        System.exit(0);
                }
                int port = -1;
                try {
                        port = Integer.parseInt(args[0]);
                } catch (NumberFormatException nfe) {
                        System.out.println("[Port] must be an integer");
                        System.exit(2);
                }
                Socket clientSock;
                ServerSocket sock = new ServerSocket(port);
                System.out.println("Server ready for connections");

                int bufLen = 1024;
                byte clientInput[] = new byte[bufLen]; // up to 1024 bytes in a message.
                while(true) {
                        System.out.println("Server waiting for a connection");
                        clientSock = sock.accept(); // blocking wait
                        PrintWriter out = new PrintWriter(clientSock.getOutputStream(), true);
                        InputStream input = clientSock.getInputStream();
                        System.out.println("Server connected to client");
                        int numr = input.read(clientInput, 0, bufLen);
                        while (numr != -1) {
                          String received = new String(clientInput, 0, numr);
                          System.out.println("read from client: " + received);
                          out.println(received);
                          numr = input.read(clientInput, 0, bufLen);
                        }
                        input.close();
                        clientSock.close();
                        System.out.println("Socket Closed.");
                }
        } catch(Exception e) {
                e.printStackTrace();
        }
}
}
