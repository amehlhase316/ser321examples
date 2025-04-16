import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import echo.*;
import java.util.Scanner;

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final CalcGrpc.CalcBlockingStub blockingStub2;

  /** Construct client for accessing server using the existing channel. */
  public EchoClient(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2 = CalcGrpc.newBlockingStub(channel);
  }

  public void askServerToParrot(String message) {
    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = blockingStub.parrot(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
    System.out.println("Received from server: " + response.getMessage());
  }

  private void add(int a, int b) {
    echo.CalcRequest req = echo.CalcRequest.newBuilder().setNum1(a).setNum2(b).build();
    echo.CalcResp response;
    try {
      response = blockingStub2.add(req);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
  }

  private void sub(int a, int b) {
    echo.CalcRequest req = echo.CalcRequest.newBuilder().setNum1(a).setNum2(b).build();
    echo.CalcResp response;
    try {
      response = blockingStub2.sub(req);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
  }


  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.out.println("Expected arguments: <host(String)> <port(int)> <message(String)>");
      System.exit(1);
    }
    int port = 9099;
    String host = args[0];
    String message = args[2];
    try {
      port = Integer.parseInt(args[1]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }
    

    // Create a communication channel to the server, known as a Channel. Channels are thread-safe
    // and reusable. It is common to create channels at the beginning of your application and reuse
    // them until the application shuts down.
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build();
    try {
      Scanner scanner = new Scanner(System.in);
      EchoClient client = new EchoClient(channel);
      client.askServerToParrot(message);
      while (true) {
        System.out.print("Add first number (0 to exit): ");
        int a = scanner.nextInt();
        if (a == 0) {
          break;
        }
        System.out.print("Add second number: ");
        int b = scanner.nextInt();
        client.add(a, b);
      }
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
      // resources the channel should be shut down when it will no longer be used. If it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

}
