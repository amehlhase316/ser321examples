package example.grpcclient;

import java.util.ArrayList;
import service.*;

public class RegistryAnswerImpl extends RegistryGrpc.RegistryImplBase {
  private ArrayList<String> services;
  public RegistryAnswerImpl(ArrayList<String> services) {
    super();
    this.services = services;
  }
  
  @Override
  public void getServices(service.GetServicesReq req,
      io.grpc.stub.StreamObserver<service.ServicesListRes> responseObserver) {
    System.out.println("Received from client: GetServices");
    ServicesListRes response = ServicesListRes.newBuilder().setIsSuccess(true).addAllServices(services).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
