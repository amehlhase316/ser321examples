package mergeSort;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;
import org.json.JSONTokener;

public class NetworkUtils {
  /**
   * Performs a request on a remote node and waits for a reply which it rebuilds into a message
   * 
   * @param message to send to remote node
   * @return the reply message it read back
   */
  public static JSONObject send(int port, JSONObject message) {
    Socket socket = null;
    try {
      // open socket
      socket = new Socket("localhost", port);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      
      // write message
      out.println(message.toString());
      // expect message in reply
      String line = in.readLine();
      JSONTokener tokener = new JSONTokener(line);
      JSONObject root = new JSONObject(tokener);
      
      // cleanup
      in.close();
      out.close();
      socket.close();
      
      // give back reply
      return root;
    } catch (SocketException | EOFException e) {
      // client disconnect
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (socket != null)
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    return null;
  }
  
  public static JSONObject read(Socket conn) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line = in.readLine();
    JSONTokener tokener = new JSONTokener(line);
    JSONObject root = new JSONObject(tokener);
    return root;
  }
  
  public static void respond(Socket conn, JSONObject message) throws IOException {
    PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
    out.println(message.toString());
  }
}
