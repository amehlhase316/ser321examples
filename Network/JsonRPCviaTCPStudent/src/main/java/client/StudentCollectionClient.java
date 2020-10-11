package client;

import java.io.*;
import java.util.*;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

import server.Student;
import server.StudentCollection;
import client.StudentTcpProxy;

/**
 * Purpose:
 * A Java class and main method demonstrating an approach to calling
 * JsonRpc methods where argument and return types are user-defined classes,
 * and communication between client and server is done using TCP/IP sockets.
 *
 * @author Tim Lindquist (tim.lindquist@asu.edu), ASU Software Engineering
 * @version April 2020
 
 */
public class StudentCollectionClient extends Object {

   static final boolean debugOn = false;

   private static void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

   public static void main(String args[]) {

      String host = "localhost";
      String port = "8080";
      
      try {
         // setup connection
         if(args.length >= 2){
            host = args[0];
            port = args[1];
         }
         String url = "http://"+host+":"+port+"/";
         System.out.println("Opening connection to: "+url);
         StudentTcpProxy sc = (StudentTcpProxy)new StudentTcpProxy(host, Integer.parseInt(port));

         // get input
         BufferedReader stdin = new BufferedReader(
            new InputStreamReader(System.in));
         System.out.print("Enter end or {add|get|getNameById|getNames|remove} followed by args>");
         String inStr = stdin.readLine();
         StringTokenizer st = new StringTokenizer(inStr);
         String opn = st.nextToken();
         // process input
         while(!opn.equalsIgnoreCase("end")) {
            if(opn.equalsIgnoreCase("add")){
               String name = "";
               while(st.hasMoreTokens()){
                  name = name + st.nextToken();
                  if(st.hasMoreTokens()) name = name + " ";
               }
               Student aStud = new Student(name,7,new String[]{"Ser423","Ser321"});
               boolean result = sc.add(aStud);
               System.out.println("Add "+aStud.name+" result "+result);
            }else if (opn.equalsIgnoreCase("get")) {
               String name = "";
               while(st.hasMoreTokens()){
                  name = name + st.nextToken();
                  if(st.hasMoreTokens()) name = name + " ";
               }
               Student result = sc.get(name);
               System.out.println("Got "+result.toString());
            }else if (opn.equalsIgnoreCase("getNames")) {
               String[] result = sc.getNames();
               System.out.print("The collection has entries for: ");
               for (int i = 0; i < result.length; i++){
                  System.out.print(result[i]+", ");
               }
               System.out.println();
            }else if (opn.equalsIgnoreCase("remove")) {
               String name = st.nextToken();
               while(st.hasMoreTokens()){
                  name = name + " " + st.nextToken();
               }
               boolean result = sc.remove(name);
               System.out.println("remove "+name+" result "+result);
            }else if (opn.equalsIgnoreCase("getNamebyid")) {
               int idNo = Integer.parseInt(st.nextToken());
               String result = sc.getNameById(idNo);
               System.out.println(result+" has id number "+idNo);
            }
            System.out.print("Enter end or {add|get|getNameById|getNames|remove} followed by args>");
            inStr = stdin.readLine();
            st = new StringTokenizer(inStr);
            opn = st.nextToken();
         }
      }catch (Exception e) {
         e.printStackTrace();
         System.out.println("Oops, you didn't enter the right stuff");
      }
   }
}
