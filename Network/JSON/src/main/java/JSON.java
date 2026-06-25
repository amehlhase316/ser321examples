import java.io.*;
import org.json.*;


public class JSON {
   public static void main(final String args[]) {
         // create a JSON String
         String json3 = "{'Organization':'ASU','Address':{'first':'Poly','second':'Tempe'},'employees':[{ 'firstName':'John', 'lastName':'Doe' },{ 'firstName':'Anna', 'lastName':'Smith' },{ 'firstName':'Peter', 'lastName':'Jones' }]}";
         
         // Json string to JSONObject
         JSONObject newObject = new JSONObject(json3);
         System.out.println(newObject.getString("Organization"));
         System.out.println(newObject.getJSONObject("Adress").getString("first"));

         // create new JSON Array which pulles the employees from the newObject
         JSONArray employeeArray = newObject.getJSONArray("employees");
         System.out.println(employeeArray);

         // create a new JSON array in which we will save all the first names
         JSONArray justFirstnames = new JSONArray();
         for(int i = 0; i < employeeArray.length(); i++){
            System.out.println(employeeArray.getJSONObject(i).getString("firstName"));
            JSONObject newName = new JSONObject();
            newName.put("name", employeeArray.getJSONObject(i).getString("firstName"));
            justFirstnames.put(newName);
         }
         
         try {
            // creates a FileWriter Object
            FileWriter writer = new FileWriter("names.json"); 
            
            // Writes the content to the file
            writer.write(justFirstnames.toString()); 
            writer.flush();
            writer.close();

         }  catch (Exception e) {
            System.out.println("exception: " + e.getMessage());
            e.printStackTrace();
         }
   }
}
