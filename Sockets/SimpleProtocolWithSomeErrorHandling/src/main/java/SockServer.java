import org.json.JSONArray;
import org.json.JSONObject;

import java.net.*;
import java.io.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version August 2020
 *
 */
public class SockServer {
  static Socket sock;
  static DataOutputStream os;
  static ObjectInputStream in;

  public static void main (String args[]) {

    try {
      //open socket
      ServerSocket serv = new ServerSocket(8888); // create server socket on port 8888
      System.out.println("Server ready for connections");

      while (true){
        System.out.println("Server waiting for a connection");
        sock = serv.accept(); // blocking wait

        // setup the object reading channel
        in = new ObjectInputStream(sock.getInputStream());

        // get output channel
        OutputStream out = sock.getOutputStream();

        // create an object output writer (Java only)
        os = new DataOutputStream(out);

        String s = (String) in.readObject();
        JSONObject req = new JSONObject(s);

        JSONObject res = testField(req, "type", "java.lang.String");
        if (!res.getBoolean("ok")) {
          overandout(res);
          continue;
        }

        // check which request it is (could also be a switch statement)
        if (req.getString("type").equals("echo")) {
          res = echo(req);
        } else if (req.getString("type").equals("add")) {
          res = add(req);
        } else if (req.getString("type").equals("addmany")) {
          res = addmany(req);
        } else {
          res = wrongType(req);
        }
        overandout(res);
      }
    } catch(Exception e) {e.printStackTrace();}
  }

  static JSONObject testField(JSONObject req, String key, String type){
    JSONObject res = new JSONObject();

    // field does not exist
    if (!req.has(key)){
      res.put("ok", false);
      res.put("message", "Field " + key + " does not exist in request");
      return res;
    }
  System.out.println(req.get(key).getClass().getName());
    // field does not have correct type
    if (!req.get(key).getClass().getName().equals(type)){
      res.put("message", "Field " + key + " needs to be of type: " + type);
      res.put("ok", false);
      return res.put("ok", false);
    } else {
      return res.put("ok", true);
    }
  }

  static JSONObject echo(JSONObject req){
    JSONObject res = testField(req, "data", "java.lang.String");

    if (res.getBoolean("ok")) {
      res.put("type", "echo");
      res.put("result", "Here is your echo: " + req.getString("data"));
    }
    return res;
  }

  static JSONObject add(JSONObject req){
    JSONObject res1 = testField(req, "num1", "java.lang.Integer");
    if (!res1.getBoolean("ok")) {
      return res1;
    }

    JSONObject res2 = testField(req, "num2", "java.lang.Integer");
    if (!res2.getBoolean("ok")) {
      return res2;
    }

    JSONObject res = new JSONObject();
    res.put("ok", true);
    res.put("type", "add");
    res.put("result", req.getInt("num1") + req.getInt("num2"));
    return res;
  }

  static JSONObject addmany(JSONObject req){
    JSONObject res = testField(req, "nums", "org.json.JSONArray");
    if (!res.getBoolean("ok")) {
      return res;
    }

    int result = 0;
    JSONArray array = req.getJSONArray("nums");
    for (int i = 0; i < array.length(); i ++){
      try{
      result += array.getInt(i);
      } catch (org.json.JSONException e){
        res.put("ok", false);
        res.put("message", "Values in array need to be ints");
        return res;
      }
    }

    res.put("ok", true);
    res.put("type", "addmany");
    res.put("result", result);
    return res;
  }

  static JSONObject wrongType(JSONObject req){
    JSONObject res = new JSONObject();
    res.put("ok", false);
    res.put("message", "Type " + req.getString("type") + " not supported.");
    return res;
  }

  static void overandout(JSONObject res) {
    try {
      os.writeUTF(res.toString());
      // make sure it wrote and doesn't get cached in a buffer
      os.flush();

      os.close();
      in.close();
      sock.close();
    } catch(Exception e) {e.printStackTrace();}

  }
}