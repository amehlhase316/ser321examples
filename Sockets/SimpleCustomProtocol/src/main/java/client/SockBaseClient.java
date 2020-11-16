package client;

import java.net.*;
import java.io.*;
import java.util.Map;

import java.nio.file.Paths;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;


class SockBaseClient {
     

     public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
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
        
        // read JSON data from the file
        JSONObject data = null;
        try {
          data = readJson(filename);
          System.out.println(data);
        } catch (IOException ex) {
          ex.printStackTrace();
        } catch (JSONException ex) {
          ex.printStackTrace();
        }

        try {
            // connect to the server
            serverSock = new Socket(host, port);

            // write to the server
            out = new ObjectOutputStream(serverSock.getOutputStream());
            out.writeObject(data.toString());

            // read from the server
            in = new ObjectInputStream(serverSock.getInputStream());

            String parsedResult = "";
            String responseType = data.getJSONObject("header").getString("response");
            responseType = responseType.toLowerCase();

            System.out.println("Requested response type: " + responseType.toUpperCase());

            if (responseType.equals("json")){
                //getting result from JSON
                parsedResult = String.valueOf(new JSONObject((String) in.readObject()).get("result"));
            }
            else {
                parsedResult = (String) in.readObject();
            }
            

            System.out.println("Result is " + parsedResult);
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
    
}

