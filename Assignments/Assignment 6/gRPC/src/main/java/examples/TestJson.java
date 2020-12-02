package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONObject;
import org.json.JSONTokener;

public class TestJson {
  public static void Execute(JSONObject request) {
    try (Socket socket = new Socket("localhost", 9001)) {
      send(socket.getOutputStream(), request);
      JSONObject response = read(socket.getInputStream());
      System.out.println(response.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) throws IOException {
    // base empty check
    Execute(getServices());

    // Register Self
    Execute(register("Json", "localhost", 80, 10020));
    // get thread's services
    Execute(getServices());
    // get random
    Execute(findServer("random"));
    // get all random
    Execute(findServers("random"));

    // Register Self
    Execute(register("Json", "localhost", 81, 10021));
    // get thread's services
    Execute(getServices());
    // get random
    Execute(findServer("services.Joke/setJoke"));
    // get all random
    Execute(findServers("random"));

    // get tictac
    Execute(findServer("tictac"));

    // get does not exist
    Execute(findServer("asdf"));
  }
  
  public static JSONObject read(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String line = reader.readLine();
    JSONTokener tokener = new JSONTokener(line);
    JSONObject response = new JSONObject(tokener);
    return response;
  }
  
  public static void send(OutputStream out, JSONObject request) throws IOException {
    PrintWriter writer = new PrintWriter(out, true);
    writer.println(request.toString());
  }

  public static JSONObject getServices() {
    JSONObject response = new JSONObject();
    response.put("requestType", "GetServices");
    return response;
  }

  public static JSONObject register(String protocol, String uri, int port, int discoveryPort) {
    JSONObject response = new JSONObject();
    response.put("requestType", "Register");
    response.put("protocol", protocol);
    response.put("uri", uri);
    response.put("port", port);
    response.put("discoveryPort", discoveryPort);
    return response;
  }

  public static JSONObject findServer(String name) {
    JSONObject response = new JSONObject();
    response.put("requestType", "FindServer");
    response.put("serviceName", name);
    return response;
  }

  public static JSONObject findServers(String name) {
    JSONObject response = new JSONObject();
    response.put("requestType", "FindServers");
    response.put("serviceName", name);
    return response;
  }
}
