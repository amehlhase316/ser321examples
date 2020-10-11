package server;

import java.util.Hashtable;
import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.Enumeration;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

/**
 * Purpose: This class is part of an example developed to demonstrate distributed
 * and mobile apps connecting to a JsonRPC server where the client and server
 * communicate using TCP/IP stream-based sockets.
 *
 * @author Tim Lindquist
 * @version April 2020
 *
 **/
class StudentCollectionImpl extends Object implements StudentCollection{

   public Hashtable<String,Student> students;
   private static final boolean debugOn = false;
   private static final String studentJsonFileName = "students.json";

   public StudentCollectionImpl() {
      debug("creating a new student collection");
      students = new Hashtable<String,Student>();
      try{
         this.resetFromJsonFile();
      }catch(Exception ex){
         System.out.println("error resetting from students json file"+
                            ex.getMessage());
      }
   }

   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

   public boolean resetFromJsonFile() {
      boolean ret = true;
      try{
         students.clear();
         String fileName = studentJsonFileName;
         File f = new File(fileName);
         FileInputStream is = new FileInputStream(f);
         JSONObject studentMap = new JSONObject(new JSONTokener(is));
         Iterator<String> it = studentMap.keys();
         while (it.hasNext()){
            String mType = it.next();
            JSONObject studentJson = studentMap.optJSONObject(mType);
            Student stud = new Student(studentJson);
            students.put(stud.name, stud);
            debug("added "+stud.name+" : "+stud.toJsonString()+
                  "\nstudents.size() is: " + students.size());
         }
      }catch (Exception ex){
         System.out.println("Exception reading json file: "+ex.getMessage());
         ret = false;
      }
      return ret;
   }

   public boolean saveToJsonFile() {
      boolean ret = true;
      try {
         String jsonStr;
         JSONObject obj = new JSONObject();
         for (Enumeration<String> e = students.keys(); e.hasMoreElements();){
            Student aStud = students.get((String)e.nextElement());
            obj.put(aStud.name,aStud.toJson());
         }
         PrintWriter out = new PrintWriter(studentJsonFileName);
         out.println(obj.toString(2));
         out.close();
      }catch(Exception ex){
         ret = false;
      }
      return ret;
   }
   
   public boolean add(Student aStud) {
      boolean ret = true;
      debug("adding student named: "+((aStud==null)?"unknown":aStud.name));
      try{
         students.put(aStud.name,aStud);
      }catch(Exception ex){
         ret = false;
      }
      return ret;
   }

   public boolean remove(String aName) {
      debug("removing student named: "+aName);
      return ((students.remove(aName)==null)?false:true);
   }

   public String[] getNames() {
      String[] ret = {};
      debug("getting "+students.size()+" student names.");
      if(students.size()>0){
         ret = (String[])(students.keySet()).toArray(new String[0]);
      }
      return ret;
   }
   
   public String getNameById(int id) {
      String ret = "unknown";
      String[] keys = (String[])(students.keySet()).toArray(new String[0]);
      for (int i=0; i< keys.length; i++){
         Student aStud = students.get(keys[i]);
         if (aStud.studentid==id){
            ret = aStud.name;
            break;
         }
      }
      return ret;
   }
   
   public Student get(String aName) {
      Student ret = new Student("unknown",0,new String[]{"empty"});
      Student aStud = students.get(aName);
      if (aStud != null) {
         ret = aStud;
      }
      return ret;
   }

}
