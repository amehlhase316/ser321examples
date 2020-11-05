package client;

import java.net.*;
import java.io.*;

// import org.json.JSONObject;
// import org.json.parser.JSONParser;
// import org.json.JSONTokener;
import org.json.*;

import buffers.OperationProtos.Operation;
import buffers.ResponseProtos.Response;

class SockBaseClient {
     

     public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int i1=0, i2=0;
        int port = 9099; // default port

        if (args.length != 3) {
            System.out.println("Expected arguments: <host(String)> <port(int)> <data(json file)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }
        String filename = args[2];
        Operation op = null;
        
        // read JSON data from the file
        JSONObject data = null;
        try {
          data = readJson(filename);
          op = generateObjectFromPB(data);
        } catch (IOException ex) {
          ex.printStackTrace();
        } catch (JSONException ex) {
          ex.printStackTrace();
        }

        try {
            // connect to the server
            serverSock = new Socket(host, port);

            // write to the server
            out = serverSock.getOutputStream();
            op.writeDelimitedTo(out);

            // read from the server
            in = serverSock.getInputStream();
            Response response = Response.parseDelimitedFrom(in);

            System.out.println("Result is " + response.getResultString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }

    private static JSONObject readJson(String filename) throws IOException, JSONException {
      // read json from build directory, so the getResource is needed
      File file = new File(
        SockBaseClient.class.getResource("/"+filename).getFile()
      );
      Reader reader = new FileReader(file);
      JSONTokener jsonTokener = new JSONTokener(reader);
      return new JSONObject(jsonTokener);
    }

    private static Operation generateObjectFromPB(JSONObject data) {
      JSONObject header = (JSONObject)data.get("header");
      JSONObject payload = (JSONObject)data.get("payload");

      // create protobuf object
      Operation.Builder op = Operation.newBuilder()
        .setVal1((String)payload.get("num1"))
        .setVal2((String)payload.get("num2"))
        .setBase((String)header.get("base"))
        .setOperationType(getOperationType((String)header.get("operation")))
        .setResponseType(getResponseType((String)header.get("response")));
      return op.build();
    } 

    private static Operation.OperationType getOperationType (String _type) throws RuntimeException {
      if (_type.toLowerCase().equals("add"))
        return Operation.OperationType.ADD;
      else if (_type.toLowerCase().equals("sub"))
        return Operation.OperationType.SUB;
      else if (_type.toLowerCase().equals("mul"))
        return Operation.OperationType.MUL;
      else if (_type.toLowerCase().equals("div"))
        return Operation.OperationType.DIV;
      else
        throw new java.lang.RuntimeException("Operation type not found");
    }

  private static Operation.ResponseType getResponseType (String _type) throws RuntimeException {
      if (_type.toLowerCase().equals("json"))
        return Operation.ResponseType.JSON;
      if (_type.toLowerCase().equals("string"))
        return Operation.ResponseType.STRING;
      else
        throw new java.lang.RuntimeException("Response type not found");
    }
}

