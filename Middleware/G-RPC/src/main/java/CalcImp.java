import io.grpc.stub.StreamObserver;
import echo.*;

public class CalcImp extends CalcGrpc.CalcImplBase{

    @Override
    public void add(echo.CalcRequest req, StreamObserver<echo.CalcResp> responseObserver) {
        CalcResp response = CalcResp.newBuilder().setResult(req.getNum1()+req.getNum2()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sub(echo.CalcRequest req, StreamObserver<echo.CalcResp> responseObserver) {
        CalcResp response = CalcResp.newBuilder().setResult(req.getNum1()-req.getNum2()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mult(echo.CalcRequest req, StreamObserver<echo.CalcResp> responseObserver) {
        CalcResp response = CalcResp.newBuilder().setResult(req.getNum1()*req.getNum2()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

