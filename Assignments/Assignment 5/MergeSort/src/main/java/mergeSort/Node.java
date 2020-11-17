package mergeSort;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONObject;

public abstract class Node implements Runnable {
  private int _port;

  public Node(int port) {
    _port = port;
  }

  public abstract JSONObject init(JSONObject object);

  public abstract JSONObject peek(JSONObject object);

  public abstract JSONObject remove(JSONObject object);

  public abstract JSONObject error(String error);

  @Override
  public void run() {
    // separated so the finally can clean up the connection
    ServerSocket socket = null;
    try {
      // create the listening socket
      socket = new ServerSocket(_port);
      while (true) { // handle connections indefinitely
        Socket conn = null;
        try {
          // listen for connection
          conn = socket.accept();

          // read in a message
          JSONObject root = NetworkUtils.read(conn);

          JSONObject ret = error("");
          if (root.has("method")) {
            switch (root.getString("method")) {
            case ("init"):
              ret = init(root);
              break;
            case ("peek"):
              ret = peek(root);
              break;
            case ("remove"):
              ret = remove(root);
              break;
            }
          }

          NetworkUtils.respond(conn, ret);

          // cleanup
          conn.close();
        } catch (SocketException | EOFException e) {
          // expected on timeout
        } catch (IOException ex) {
          ex.printStackTrace();
        } finally {
          // cleanup, just in case
          if (conn != null)
            try {
              conn.close();
            } catch (IOException ex) {
              ex.printStackTrace();
            }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // cleanup, just in case
      if (socket != null)
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
  }
}
