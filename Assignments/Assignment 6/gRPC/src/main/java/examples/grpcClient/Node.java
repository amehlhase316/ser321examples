package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 * Server that manages startup/shutdown of the `Node`.
 */
public class Node {
  static private Server server;
  int port;

  ServerSocket serv = null;
  InputStream in = null;
  OutputStream out = null;
  Socket clientSocket = null;

  net.Network network = null;

  Node(int port) {
    this.port = port;
    this.network = new net.proto.Network();
  }

  private void start() throws IOException {
    /* The port on which the server should run */
    // Here we are adding the different services that a client can call
    ArrayList<String> services = new ArrayList<>();
    server = ServerBuilder.forPort(port)
        .addService(new EchoImpl())
        .addService(new JokeImpl())
        .addService(new RegistryAnswerImpl(services)).build().start();

    for (var service : server.getServices()) {
      // returns the services that are available from this node
      for (ServerMethodDefinition<?, ?> method : service.getMethods()) {
        services.add(method.getMethodDescriptor().getFullMethodName());
        System.out.println(method.getMethodDescriptor().getFullMethodName());
      }
    }

    System.out.println("Server running ...");
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown
        // hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          Node.this.stop();
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
        }
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon
   * threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    if (args.length != 5) {
      System.out.println("Expected arguments: <regAddr(string)> <regPort(int)> <nodeAddr> <nodePort<int>");
      System.exit(1);
    }
    int regPort = 9003;
    int nodePort = 9099;
    try {
      regPort = Integer.parseInt(args[1]);
      nodePort = Integer.parseInt(args[3]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }
    final Node server = new Node(nodePort);
    System.out.println(args[0]);
    System.out.println(args[1]);
    System.out.println(args[2]);
    System.out.println(args[3]);

    System.out.println(args[4]);

    // Comment the next 2 lines for your local client server development (Activity 2 task 1, you need this part again for Task 2)
    Register regThread = new Register(args[0], regPort, args[2], nodePort, args[4]);
    regThread.start();

    
    server.start();

    server.blockUntilShutdown();
  }

}