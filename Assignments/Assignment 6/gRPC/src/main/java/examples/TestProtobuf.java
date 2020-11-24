package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;

public class TestProtobuf {
  public static void Execute(Request request) {
    try (Socket socket = new Socket("localhost", 9000)) {
      send(socket.getOutputStream(), request);
      Response response = readResponse(socket.getInputStream());
      System.out.print(response); 
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    // base empty check
    Execute(getServices());

    // Register Self
    Execute(register("Protobuf", "localhost", 90, 9020));
    // get thread's services
    Execute(getServices());
    // get random
    Execute(findServer("random"));
    // get all random
    Execute(findServers("random"));

    // Register Self
    Execute(register("Protobuf", "localhost", 91, 9021));
    // get thread's services
    Execute(getServices());
    // get random
    Execute(findServer("random"));
    // get all random
    Execute(findServers("random"));

    // get tictac
    Execute(findServer("tictac"));

    // get does not exist
    Execute(findServer("asdf"));
  }
  
  public static Response readResponse(InputStream in) throws IOException {
    return Response.parseDelimitedFrom(in);
  }

  public static void send(OutputStream out, Request request) throws IOException {
    request.writeDelimitedTo(out);
  }
  
  public static Request getServices() {
    return Request.newBuilder().setRequestType(RequestType.GETSERVICES).build();
  }
  
  public static Request register(String type, String uri, int port, int discoveryPort) {
    Request.Connection.Builder conn = Request.Connection.newBuilder().setUri(uri).setPort(port).setDiscoveryPort(discoveryPort).setProtocol(type);
    return Request.newBuilder().setRequestType(RequestType.REGISTER).setConnection(conn).build();
  }

  public static Request findServer(String name) {
    return Request.newBuilder().setRequestType(RequestType.FINDSERVER).setServiceName(name).build();
  }

  public static Request findServers(String name) {
    return Request.newBuilder().setRequestType(RequestType.FINDSERVERS).setServiceName(name).build();
  }
}
