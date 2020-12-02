import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import echo.ServerResponse;
import echo.ClientRequest;
import echo.EchoGrpc;

/**
 * Server that manages startup/shutdown of the `EchoServer`.
 */
public class EchoServer {
  private Server server;
  int port;

  EchoServer(int port) {
    this.port = port;
  }

  private void start() throws IOException {
    /* The port on which the server should run */
    server = ServerBuilder.forPort(port)
        .addService(new EchoImpl())
        .build()
        .start();

    System.out.println("Server running ...");
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        try {
          EchoServer.this.stop();
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
   * Await termination on the main thread since the grpc library uses daemon threads.
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
    if (args.length != 1) {
      System.out.println("Expected arguments: <port(int)>");
      System.exit(1);
    }
    int port = 9099;
    try {
      port = Integer.parseInt(args[0]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }
    final EchoServer server = new EchoServer(port);
    server.start();
    server.blockUntilShutdown();
  }

  static class EchoImpl extends EchoGrpc.EchoImplBase {

    @Override
    public void parrot(ClientRequest req, StreamObserver<ServerResponse> responseObserver) {
      System.out.println("Received from client: " + req.getMessage());
      ServerResponse response = ServerResponse.newBuilder().setMessage(req.getMessage()).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}
