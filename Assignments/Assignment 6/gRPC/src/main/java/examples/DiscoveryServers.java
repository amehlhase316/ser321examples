package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DiscoveryServers {
  public static class DiscoveryThread extends Thread {
    ServerSocket serv = null;
    InputStream in = null;
    OutputStream out = null;
    Socket clientSocket = null;
    int listenPort = 9000; // default port

    ArrayList<String> services = new ArrayList<>();
    net.Network network = null;

    public DiscoveryThread(int listenPort, net.Network network) {
      this.listenPort = listenPort;
      this.network = network;
    }

    public void addService(String name) {
      services.add(name);
    }

    @Override
    public void run() {
      try {
        serv = new ServerSocket(listenPort);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(2);
      }
      try {
        while (serv.isBound() && !serv.isClosed()) {
          System.out.println("Ready...");
          try {
            clientSocket = serv.accept();
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            var request = network.readRequest(in);
            if (request.type == registry.requests.Request.Types.GetServices) {
              var res = new registry.responses.ServicesList(services);
              network.send(clientSocket.getOutputStream(), res);
            } else {
              var res = new registry.responses.Error("Unrecognized command");
              network.send(clientSocket.getOutputStream(), res);
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          } finally {
            if (out != null)
              out.close();
            if (in != null)
              in.close();
            if (clientSocket != null)
              clientSocket.close();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    net.Network protoNet = new net.proto.Network();
    
    DiscoveryThread pthread = new DiscoveryThread(9020, protoNet);
    pthread.addService("random");
    pthread.addService("lolcats");
    pthread.start();
    
    DiscoveryThread pthread2 = new DiscoveryThread(9021, protoNet);
    pthread2.addService("random");
    pthread2.addService("tictac");
    pthread2.start();

    net.Network jsonNet = new net.json.Network();
    
    DiscoveryThread thread = new DiscoveryThread(10020, jsonNet);
    thread.addService("random");
    thread.addService("lolcats");
    thread.start();

    DiscoveryThread thread2 = new DiscoveryThread(10021, jsonNet);
    thread2.addService("random");
    thread2.addService("tictac");
    thread2.start();
  }
}
