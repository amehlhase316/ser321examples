package server;

import java.net.*;
import java.io.*;

import server.Base;
import buffers.OperationProtos.Operation;
import buffers.ResponseProtos.Response;


class SockBaseServer {
    public static void main (String args[]) throws Exception {

        int count = 0;
        ServerSocket    serv = null;
        InputStream in = null;
        OutputStream out = null;
        Socket clientSocket = null;
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        if (args.length != 2) {
          System.out.println("Expected arguments: <port(int)> <delay(int)>");
          System.exit(1);
		}
        
        try {
          port = Integer.parseInt(args[0]);
          sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
          System.out.println("[Port|sleepDelay] must be an integer");
          System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch(Exception e) {
          e.printStackTrace();
          System.exit(2);
        }
        while (serv.isBound() && !serv.isClosed()) {
            System.out.println("Ready...");
            try {
                clientSocket = serv.accept();
                in = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();
                // read the proto object and put into new objct
                Operation op = Operation.parseDelimitedFrom(in);
                String result = null;
                String num1 = op.getVal1();
                String num2 = op.getVal2();
                int baseN = Integer.parseInt(op.getBase());

                Base base = new Base();

                if (op.getOperationType() == Operation.OperationType.ADD) {
                  result = base.add(num1, num2, baseN);
          System.out.println("base " + baseN + ": " + num1 + " + " + num2 + " = " + result);
                } else if (op.getOperationType() == Operation.OperationType.SUB) {
                  result = base.substract(num1, num2, baseN);
          System.out.println("base " + baseN + ": " + num1 + " - " + num2 + " = " + result);
                }
                if (op.getResponseType() == Operation.ResponseType.JSON){
                  //just building a JSON strinng
                  result = result;
                }
                Response response = buildResponse(result);
                response.writeDelimitedTo(out);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (out != null)  out.close();
                if (in != null)   in.close();
                if (clientSocket != null) clientSocket.close();
            }
        }
    }

    private static Response buildResponse(String result) {
      Response.Builder response = Response.newBuilder();
      response.setResultString(result);
      return response.build();
    }
}

