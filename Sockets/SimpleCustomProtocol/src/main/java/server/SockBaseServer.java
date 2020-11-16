package server;

import java.net.*;
import java.io.*;

import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

import server.Base;

class SockBaseServer {
    public static void main (String args[]) throws Exception {

        int count = 0;
        ServerSocket    serv = null;
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Socket clientSocket = null;
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        if (args.length != 2) {
          System.out.println("Expected arguments: <port(int)> <delay(int)>");
          System.exit(1);
		    }
        System.out.println("Running on port: " + args[0]);
        System.out.println("Sleep delay is :" + args[1] + " miliseconds");
        
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
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                String jsonData = (String) in.readObject();

                // convert json string to a JSON object
                JSONTokener jsonTokener = new JSONTokener(jsonData);
                JSONObject data = new JSONObject(jsonTokener);
                
                // get the 'header' and the 'payload'
                JSONObject headerJSON = (JSONObject)data.get("header");
                JSONObject payloadJSON = (JSONObject)data.get("payload");

                Map header = headerJSON.toMap();
                Map payload = payloadJSON.toMap();
                
                Operations operation = getOperation(header);
                Response response = getResponse(header);
                int baseN = getBase(header, "base");

                String num1 = getNum(payload, "num1");
                String num2 = getNum(payload, "num2");

                Base base = new Base();

                // String type result by default
                String result = null;

                if (operation == Operations.ADD) {
                  result = base.add(num1, num2, baseN);
		  System.out.println("base " + baseN + ": " + num1 + " + " + num2 + " = " + result);
                } else if (operation == Operations.SUB) {
                  result = base.substract(num1, num2, baseN);
		  System.out.println("base " + baseN + ": " + num1 + " - " + num2 + " = " + result);
                }

                if (response == Response.JSON){
                  //just building a JSON strinng
                  result = "{'result':" + result + "}";
                }

                out.writeObject(result);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (out != null)  out.close();
                if (in != null)   in.close();
                if (clientSocket != null) clientSocket.close();
            }
        }
    }

    private static int getBase(Map header, String key) {
      return Integer.parseInt((String) header.get(key));
    }

    private static Operations getOperation(Map header) throws RuntimeException {
      String operation = (String) header.get("operation");
      operation = operation.toLowerCase();
      if (operation.equals("add")) {
        return Operations.ADD;
      } else if (operation.equals("sub")) {
        return Operations.SUB;
      } else {
        throw new java.lang.RuntimeException("Operation not found!");
      }
    }

    private static Response getResponse(Map header) throws RuntimeException {
      String response = (String) header.get("response");
      response = response.toLowerCase();
      if (response.equals("json")) {
        return Response.JSON;
      }
      if (response.equals("string")){
        return Response.STRING;
      } else {
        throw new java.lang.RuntimeException("Response type not found!");
      }
    }

    private static String getNum(Map payload, String key) {
      return (String) payload.get(key);
    }
}

enum Operations {
  ADD,
  SUB
}
enum Response {
  JSON,
  STRING
}
