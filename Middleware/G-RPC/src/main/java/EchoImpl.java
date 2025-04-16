import io.grpc.stub.StreamObserver;
import echo.*;
class EchoImpl extends EchoGrpc.EchoImplBase {

    @Override
    public void parrot(ClientRequest req, StreamObserver<ServerResponse> responseObserver) {
        System.out.println("Received from client: " + req.getMessage());
        ServerResponse response = ServerResponse.newBuilder().setMessage(req.getMessage()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}