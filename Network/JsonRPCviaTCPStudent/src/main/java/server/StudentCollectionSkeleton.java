package server;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * A class for client-server connections with a threaded server.
 * The student collection server creates a server socket.
 * When a client request arrives, which should be a JsonRPC request, a new
 * thread is created to service the call and create the appropriate response.
 * Byte arrays are used for communication to support multiple langs.
 *
 * @author Tim Lindquist ASU Polytechnic Department of Engineering
 * @version April 2020
 */
public class StudentCollectionSkeleton extends Object {

   private static final boolean debugOn = false;
   StudentCollection studLib;

   public StudentCollectionSkeleton (StudentCollection studLib){
      this.studLib = studLib;
   }

   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

   public String callMethod(String request){
      JSONObject result = new JSONObject();
      try{
         JSONObject theCall = new JSONObject(request);
         debug("Request is: "+theCall.toString());
         String method = theCall.getString("method");
         int id = theCall.getInt("id");
         JSONArray params = null;
         if(!theCall.isNull("params")){
            params = theCall.getJSONArray("params");
         }
         result.put("id",id);
         result.put("jsonrpc","2.0");
         if(method.equals("add")){
            JSONObject studJson = params.getJSONObject(0);
            Student studToAdd = new Student(studJson);
            debug("adding stud: "+studToAdd.toJsonString());
            studLib.add(studToAdd);
            result.put("result",true);
         }else if(method.equals("remove")){
            String studName = params.getString(0);
            debug("removing student named "+studName);
            studLib.remove(studName);
            result.put("result",true);
         }else if(method.equals("getNameById")){
            int studNum = params.getInt(0);
            debug("getNameById for student number "+studNum);
            String name = studLib.getNameById(studNum);
            result.put("result",name);
         }else if(method.equals("get")){
            String studName = params.getString(0);
            Student stud = studLib.get(studName);
            JSONObject studJson = stud.toJson();
            debug("get request found: "+studJson.toString());
            result.put("result",studJson);
         }else if(method.equals("getNames")){
            String[] names = studLib.getNames();
            JSONArray resArr = new JSONArray();
            for (int i=0; i<names.length; i++){
               resArr.put(names[i]);
            }
            debug("getNames request found: "+resArr.toString());
            result.put("result",resArr);
         }else{
            debug("Unable to match method: "+method+". Returning 0.");
            result.put("result",0.0);
         }
      }catch(Exception ex){
         System.out.println("exception in callMethod: "+ex.getMessage());
      }
      return result.toString();
   }
}

