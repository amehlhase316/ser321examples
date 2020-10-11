import org.json.JSONString;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileInputStream;
import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;

/**
 * Purpose:
 * A class implementing the operations for group management.
 * <p/>
 *
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @file    Group.java
 * @date    January, 2020
 */
public class Group extends Object implements JSONString, Serializable {

   // Serial version UID is defined below. Its only needed if you want
   // to make changes to the class and still deserialize artifacts
   // generated from prior versions. Obtain this definition with:
   // serialver -classpath classes:lib/json.jar ser321.serialize.Group
   private static final long serialVersionUID = 6982142948226029575L;

   private String name; // the name of this group
   private Vector<User> users = new Vector<User>(); // the members of this group

   public Group() {
      this.name = "unknown";
   }

   public Group(String fileName){
      try{
         FileInputStream in = new FileInputStream(fileName);
         JSONObject obj = new JSONObject(new JSONTokener(in));
         String [] names = JSONObject.getNames(obj);
         System.out.print("names are: ");
         for(int j=0; j< names.length; j++){
            System.out.print(names[j]+", ");
         }
         System.out.println("");
         name = obj.getString("name");
         users = new Vector<User>();
         for (int i=0; i< names.length; i++){
            if(!names[i].equals("name")){
               User aUser = new User((JSONObject)obj.getJSONObject(names[i]));
               users.add(aUser);
            }
         }
         in.close();
      }catch (Exception ex) {
         System.out.println("Exception importing from json: "+ex.getMessage());
      }
   }

   public String toJSONString(){
      String ret;
      JSONObject obj = new JSONObject();
      obj.put("name",name);
      for (Enumeration<User> e = users.elements(); e.hasMoreElements();){
         User usr = (User)e.nextElement();
         obj.put(usr.getId(),usr.toJSONObject());
      }
      ret = obj.toString();
      //System.out.println("group tojsonstring returning string: "+ret);
      return ret;
   }

   public String getName(){
      return name;
   }

   public void setName(String aName){
      name = aName;
   }

  /**
   * Associate a new user with this authorization group.
   * Use addUserToGroup to allow a new user access to group resources.
   * @param user Is a String specifying userId to add
   * @param pwd Is a String specifying password for user.
   */
   public void addUserToGroup(String id, String pwd) {
      boolean found = false;
      for (int i = 0; i<users.size(); i++) {
         if ((users.elementAt(i)).getId().equals(id))
            found = true;
      }
      if (!found)
         users.addElement(new User(id,pwd));
   }

  /**
   * Get the name strings of all users associated in this authorization group.
   * Use getName to retrieve the name string property for the group.
   * @return The vector of userId strings.
   */
   public Vector<String> getUserNames() {
      Vector<String> ret = new Vector<String>();
      for (Enumeration e = users.elements() ; e.hasMoreElements() ;) {
         ret.addElement(((User)e.nextElement()).getId());
      }
      return ret;
   }

  /**
   * Determine whether a user is in the group.
   * Use isMember to authenticate a user password combination for this group
   * membership.
   * @param id Is a String specifying userId that is not already in the group
   * @param pwd Is a String specifying a password for id.
   * @return true if user and pwd are authorized;
   * otherwise return false.
   */
   public boolean isMember(String id, String pwd) {
      boolean match = false;
      for (Enumeration e = users.elements() ; e.hasMoreElements() ;) {
         if(((User)e.nextElement()).check(id,pwd)){
            match = true;
            break;
         }
      }
      return match;
   }

  /**
   * Print the group to standard out
   */
   public void printGroup(){
      System.out.print("Group: "+name+" has users: ");
      for (Enumeration e = users.elements() ; e.hasMoreElements() ;) {
         System.out.print(((User)e.nextElement()).getId()+", ");
      }
      System.out.println();
   }

}
