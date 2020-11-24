package example.grpcclient;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import service.RegistryGrpc;

public class Register extends Thread {

  // info where the registry port can be contacted
  String registryHost;
  int registryPort;

  // info where the service node is running
  String serviceHost;
  int servicePort;

  String name;

  public Register(String registryHost, int registryPort, String serviceHost, int servicePort, String name) {
    this.registryHost = registryHost;
    this.registryPort = registryPort;

    this.servicePort = servicePort;
    this.serviceHost = serviceHost;
    this.name = name;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(1000); // to make sure the node is up and running
      ManagedChannel channel = ManagedChannelBuilder.forAddress(registryHost, registryPort)
          // Channels are secure by default (via SSL/TLS). For the example we disable TLS
          // to avoid
          // needing certificates.
          .usePlaintext().build();
      RegistryGrpc.RegistryBlockingStub blockingStub = RegistryGrpc.newBlockingStub(channel);
      // "Grpc", serviceHost, servicePort, discoveryPort
      service.Connection oneConn = service.Connection.newBuilder().setProtocol("Grpc")
      .setUri(serviceHost).setPort(servicePort).setDiscoveryPort(servicePort).setName(name).build();

      service.ServicesListRes res = blockingStub.register(service.RegisterReq.newBuilder().setConnection(oneConn).build());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}