import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import echo.ServerResponse;
import echo.ClientRequest;
import echo.EchoGrpc;
class EchoImpl extends EchoGrpc.EchoImplBase {

    @Override
    public void parrot(ClientRequest req, StreamObserver<ServerResponse> responseObserver) {
        System.out.println("Received from client: " + req.getMessage());
        ServerResponse response = ServerResponse.newBuilder().setMessage(req.getMessage()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}