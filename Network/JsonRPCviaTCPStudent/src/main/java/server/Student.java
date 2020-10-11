package server;
/**
 * Purpose: Example Java JsonRPC server for collection of students.
 * implemented with TCP/IP
 *
 * Ser321 Distributed Apps
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 */

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;
import java.util.Arrays;

public class Student {

   private static final boolean debugOn = false;

   public String name;
   public int studentid;
   public Vector<String> takes;

   public Student(String name, int studentid, String[] courses){
      this.name = name;
      this.studentid = studentid;
      this.takes = new Vector<String>();
      this.takes.addAll(Arrays.asList(courses));
   }

   public Student(String jsonStr){
      try{
         JSONObject jo = new JSONObject(jsonStr);
         name = jo.getString("name");
         studentid = jo.getInt("studentid");
         takes = new Vector<String>();
         JSONArray ja = jo.optJSONArray("takes");
         for (int i=0; i< ja.length(); i++){
            takes.add(ja.getString(i));
         }
      }catch (Exception ex){
         System.out.println(this.getClass().getSimpleName()+
                            ": error converting from json string");
      }
   }

   public Student(JSONObject jsonObj){
      try{
         debug("constructor from json received: " + jsonObj.toString());
         name = jsonObj.optString("name","unknown");
         studentid = jsonObj.optInt("studentid",0);
         takes = new Vector<String>();
         JSONArray ja = jsonObj.getJSONArray("takes");
         for (int i=0; i< ja.length(); i++){
            takes.add(ja.getString(i));
         }
      }catch(Exception ex){
         System.out.println(this.getClass().getSimpleName()+
                            ": error converting from json string");
      }
   }

   public JSONObject toJson(){
      JSONObject jo = new JSONObject();
      try{
         jo.put("name",name);
         jo.put("studentid",studentid);
         jo.put("takes",takes);
      }catch (Exception ex){
         System.out.println(this.getClass().getSimpleName()+
                            ": error converting to json");
      }
      return jo;
   }

   public String toJsonString(){
      String ret = "";
      try{
         ret = this.toJson().toString();
      }catch (Exception ex){
         System.out.println(this.getClass().getSimpleName()+
                            ": error converting to json string");
      }
      return ret;
   }

   public String toString(){
      StringBuilder sb = new StringBuilder();
      sb.append("Student ").append(name).append(" has id ");
      sb.append(studentid).append(" and takes courses ");
      for (int i=0; i<takes.size(); i++){
         sb.append(takes.get(i)).append(" ");
      }
      return sb.toString();
   }

   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

}
