import java.io.*;
import org.json.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Map;
import java.util.LinkedHashMap;
import java.nio.charset.Charset;

public class API {
   public static void main(final String args[]) {


      try {

         //System.out.println(fetchURL("https://api.github.com/rate_limit")); // in case you need to check your rate limit
         String user = args[0];
         String json = fetchURL("https://api.github.com/users/" + user + "/repos"); // fetching the JSON reply
         System.out.println(json); // printing it so you see how it looks like 
         
         // saving it as JSON array (if it sere not an array it woudl need to be a JSONObject)
         JSONArray repoArray = new JSONArray(json);

         // new JSON which we want to save later on
         JSONArray newjSON = new JSONArray();

         // go through all the entries in the JSON array (so all the repos of the user)
         for(int i=0; i<repoArray.length(); i++){

            // now we have a JSON object, one repo 
            JSONObject repo = repoArray.getJSONObject(i);

            // get repo name
            String repoName = repo.getString("name");
            System.out.println(repoName);

            // owner is a JSON object in the repo object, get it and save it in own variable then read the login name
            JSONObject owner = repo.getJSONObject("owner");
            String ownername = owner.getString("login");
            System.out.println(ownername);

            // create a new object for the repo we want to store add the repo name and owername to it
            JSONObject newRepo = new JSONObject();
            newRepo.put("name",repoName);
            newRepo.put("owner",ownername);


            // fetch all the branches from the repo and save and branches JSONArray
            String jsonBranches = fetchURL("https://api.github.com/repos/" + user + "/" + repoName + "/branches");
            JSONArray branches = new JSONArray(jsonBranches);

            // create a new branch JSON object
            JSONArray newBranchJSON = new JSONArray();

            // iterate through all branches and save the branch name
            for(int j=0; j<branches.length(); j++){
               JSONObject branch = branches.getJSONObject(j);
               String branchName = branch.getString("name");
               System.out.println("   "+ branchName);
               JSONObject newBranch = new JSONObject();
               newBranch.put("name", branchName);

               // add new branch to branch array
               newBranchJSON.put(newBranch);
            }

            // add the branches array to the repo
            newRepo.put("branches", newBranchJSON);
            newjSON.put(newRepo);
         }

         // save shortened info into file
         PrintWriter out = new PrintWriter("repoShort.json");
         out.println(newjSON.toString());
         out.close();


      } catch (Exception e) {
         System.out.println("exception: " + e.getMessage());
         e.printStackTrace();
      }
   }


   /**
    *
    * a method to make a web request. Note that this method will block execution
    * for up to 20 seconds while the request is being satisfied. Better to use a
    * non-blocking request.
    * 
    * @param aUrl the String indicating the query url for the OMDb api search
    * @return the String result of the http request.
    *
    **/
   public static String fetchURL(final String aUrl) {
      final StringBuilder sb = new StringBuilder();
      URLConnection conn = null;
      InputStreamReader in = null;
      try {
         final URL url = new URL(aUrl);
         conn = url.openConnection();
         if (conn != null)
            conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
         if (conn != null && conn.getInputStream() != null) {
            in = new InputStreamReader(conn.getInputStream(), Charset.defaultCharset());
            final BufferedReader br = new BufferedReader(in);
            if (br != null) {
               int ch;
               // read the next character until end of reader
               while ((ch = br.read()) != -1) {
                  sb.append((char) ch);
               }
               br.close();
            }
         }
         in.close();
      } catch (final Exception ex) {
         System.out.println("Exception in url request:" + ex.getMessage());
      }
      return sb.toString();
   }
}
