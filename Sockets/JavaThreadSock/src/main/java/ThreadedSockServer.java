import java.net.*;
import java.io.*;
import java.util.*;

/**
 * A class for simple client-server connections with a threaded server. This
 * example uses Object Input and Output streams to communicate between client
 * and server.
 *
 * Ser321 Foundations of Distributed Software Systems see
 * http://pooh.poly.asu.edu/Ser321
 * 
 * @author Tim Lindquist Tim.Lindquist@asu.edu Software Engineering, CIDSE,
 *         IAFSE, ASU Poly
 * @version April 2020
 * 
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 * 
 */
public class ThreadedSockServer extends Thread {
  private Socket conn;
  private int id;
  private String buf[] = { "The Object class also has support for wait",
      "If the timer has expired, the thread continues", "This call can cause some overhead in programs",
      "Notify signals a waiting thread to wake up", "Wait blocks the thread and releases the lock" };

  public ThreadedSockServer(Socket sock, int id) {
    this.conn = sock;
    this.id = id;
  }

  public void run() {
    try {
      // setup read/write channels for connection
      ObjectInputStream in = new ObjectInputStream(conn.getInputStream());
      ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());

      // read the digit being send
      String s = (String) in.readObject();
      int index;
      // while client hasn't ended
      while (!s.equals("end")) {
        Boolean validInput = true;

        // checks if input only contains digits
        if (!s.matches("\\d+")) {
          validInput = false;
          out.writeObject("Not a number: https://gph.is/2yDymkn");
        }

        // if it contains only numbers
        if (validInput) {
          // convert to an integer
          index = Integer.valueOf(s);
          System.out.println("From client " + id + " get string " + index);
          if (index > -1 & index < buf.length) {
            // if valid, pull the line from the buffer array above and write it to socket
            out.writeObject(buf[index]);
          } else if (index == 5) {
            // fun surprise for mostly correct
            out.writeObject("Close but out of range: https://youtu.be/dQw4w9WgXcQ");
          } else {
            // really wrong
            out.writeObject("index out of range");
          }
        }
        //  wait for next token from the user
        s = (String) in.readObject();
      }
      // on close, clean up 
      System.out.println("Client " + id + " closed connection.");
      in.close();
      out.close();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) throws IOException {
    Socket sock = null;
    int id = 0;
    try {
      if (args.length != 1) {
        System.out.println("Usage: gradle ThreadedSockServer --args=<port num>");
        System.exit(0);
      }
      int portNo = Integer.parseInt(args[0]);
      if (portNo <= 1024)
        portNo = 8888;
      ServerSocket serv = new ServerSocket(portNo);
      while (true) {
        System.out.println("Threaded server waiting for connects on port " + portNo);
        sock = serv.accept();
        System.out.println("Threaded server connected to client-" + id);
        // create thread
        ThreadedSockServer myServerThread = new ThreadedSockServer(sock, id++);
        // run thread and don't care about managing it
        myServerThread.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (sock != null) sock.close();
    }
  }
}
