import java.net.*;
import java.io.*;
import java.util.*;

/**
 * A class for client-server connections with a threaded server. The echo server
 * creates a server socket. Once a client arrives, a new thread is created to
 * service all client requests for the connection. The example includes a java
 * client and a C# client. If C# weren't involved, the server and client could
 * use a bufferedreader, which allows readln to be used, and printwriter, which
 * allows println to be used. These avoid playing with byte arrays and
 * encodings. See the Java Tutorial for an example using buffered reader and
 * printwriter.
 *
 * Ser321 Foundations of Distributed Software Systems
 * 
 * @author Tim Lindquist Tim.Lindquist@asu.edu Software Engineering, CIDSE,
 *         IAFSE, ASU Poly
 * @version March 2020
 */
public class ThreadedEchoServer extends Thread {
  private static int bufLen = 1024;
  private Socket conn;
  private int id;

  public ThreadedEchoServer(Socket sock, int id) {
    this.conn = sock;
    this.id = id;
  }

  public void run() {
    try {
      OutputStream outSock = conn.getOutputStream();
      InputStream inSock = conn.getInputStream();
      byte clientInput[] = new byte[bufLen]; // up to 1024 bytes in a message.
      int numr = inSock.read(clientInput, 0, bufLen);
      while (numr != -1) {
        // System.out.println("read "+numr+" bytes");
        String clientString = new String(clientInput, 0, numr);
        System.out.println("read from client: " + id + " the string: " + clientString);
        outSock.write(clientInput, 0, numr);
        numr = inSock.read(clientInput, 0, bufLen);
      }
      inSock.close();
      outSock.close();
      conn.close();
    } catch (IOException e) {
      System.out.println("Can't get I/O for the connection.");
    }
  }

  public static void main(String args[]) {
    Socket sock;
    int id = 0;
    try {
      if (args.length != 1) {
        System.out.println("Usage: java ser321.sockets.ThreadedEchoServer" + " [portNum]");
        System.exit(0);
      }
      int portNo = Integer.parseInt(args[0]);
      if (portNo <= 1024)
        portNo = 8888;
      ServerSocket serv = new ServerSocket(portNo);
      while (true) {
        System.out.println("Echo server waiting for connects on port " + portNo);
        sock = serv.accept();
        System.out.println("Echo server connected to client: " + id);
        ThreadedEchoServer myServerThread = new ThreadedEchoServer(sock, id++);
        myServerThread.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
