package client;

import server.Student;
import server.StudentCollection;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * A class for client-server connections with a threaded server.
 * The student collection client proxy implements the server methods
 * by marshalling/unmarshalling parameters and results and using a TCP
 * connection to request the method be executed on the server.
 * Byte arrays are used for communication to support multiple langs.
 *
 * @author Tim Lindquist ASU Polytechnic Department of Engineering
 * @version April 2020
 */
public class StudentTcpProxy extends Object implements StudentCollection {

   private static final boolean debugOn = false;
   private static final int buffSize = 4096;
   private static int id = 0;
   private String host;
   private int port;
   
   public StudentTcpProxy (String host, int port){
      this.host = host;
      this.port = port;
   }

   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

   /**
    * Generic method caller using TCP
    * @param method
    * @param params
    * @return
    */
   public String callMethod(String method, Object[] params){
      // create JSON RPC message
      JSONObject theCall = new JSONObject();
      String ret = "{}";
      try{
         debug("Request is: "+theCall.toString());
         theCall.put("method",method);
         theCall.put("id",id);
         theCall.put("jsonrpc","2.0");
         ArrayList<Object> al = new ArrayList();
         for (int i=0; i<params.length; i++){
            al.add(params[i]);
         }
         JSONArray paramsJson = new JSONArray(al);
         theCall.put("params",paramsJson);

         // prep connection and the byte array
         Socket sock = new Socket(host,port);
         OutputStream os = sock.getOutputStream();
         InputStream is = sock.getInputStream();
         int numBytesReceived;
         int bufLen = 1024;
         String strToSend = theCall.toString();
         byte bytesReceived[] = new byte[buffSize];
         byte bytesToSend[] = strToSend.getBytes();

         // send the message
         os.write(bytesToSend,0,bytesToSend.length);
         // get response
         numBytesReceived = is.read(bytesReceived,0,bufLen);
         ret = new String(bytesReceived,0,numBytesReceived);
         
         // return
         debug("callMethod received from server: "+ret);
         os.close();
         is.close();
         sock.close();
      }catch(Exception ex){
         System.out.println("exception in callMethod: "+ex.getMessage());
      }
      return ret;
   }

   public boolean saveToJsonFile() {
      boolean ret = false;
      String result = callMethod("saveToJsonFile", new Object[]{});
      JSONObject res = new JSONObject(result);
      ret = res.optBoolean("result",false);
      return ret;
   }
   
   public boolean resetFromJsonFile() {
      boolean ret = false;
      String result = callMethod("resetFromJsonFile", new Object[]{});
      JSONObject res = new JSONObject(result);
      ret = res.optBoolean("result",false);
      return ret;
   }
   
   public boolean add(Student stud) {
      boolean ret = false;
      String result = callMethod("add", new Object[]{stud.toJson()});
      JSONObject res = new JSONObject(result);
      ret = res.optBoolean("result",false);
      return ret;
   }
   
   public boolean remove(String aName) {
      boolean ret = false;
      String result = callMethod("remove", new Object[]{aName});
      JSONObject res = new JSONObject(result);
      ret = res.optBoolean("result",false);
      return ret;
   }
   
   public Student get(String aName) {
      Student ret = new Student("unknown",-999, new String[]{"unknown"});
      String result = callMethod("get", new Object[]{aName});
      JSONObject res = new JSONObject(result);
      JSONObject studJson = res.optJSONObject("result");
      ret = new Student(studJson);
      return ret;
   }
   
   public String getNameById(int id) {
      String ret = "unknown";
      String result = callMethod("getNameById", new Object[]{id});
      JSONObject res = new JSONObject(result);
      ret = res.optString("result","unknown");
      return ret;
   }
   
   public String[] getNames() {
      String[] ret = new String[]{};
      String result = callMethod("getNames", new Object[0]);
      debug("result of getNames is: "+result);
      JSONObject res = new JSONObject(result);
      JSONArray namesJson = res.optJSONArray("result");
      ret = new String[namesJson.length()];
      for (int i=0; i<namesJson.length(); i++){
         ret[i] = namesJson.optString(i,"unknown");
      }
      return ret;
   }
}

