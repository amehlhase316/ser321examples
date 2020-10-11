import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

/**
 * Purpose:
 * A class to serialize and de-serialize a User based on its command line argument.
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @file    UserFileSerialize.java
 * @date    January, 2020
 */
public class UserFileSerialize {
  public static void main (String args[]) {

     String which = "write";
     if (args.length != 1) {
        System.out.println("Expected Arguments: <operation(String)[write|read]>");
          System.exit(0);
     }
     which = args[0];
     try {
        if(which.equalsIgnoreCase("write")){
          User joe = new User("Joe","hisWord");
          File outFile = new File("user.ser");
          ObjectOutputStream os = 
             new ObjectOutputStream(new FileOutputStream(outFile));
          os.writeObject(joe);
          os.flush();
          os.close();
          System.out.println("Done externalizing "+joe.toString()+" to file user.ser");
        }else if(which.equalsIgnoreCase("read")){
           File inFile = new File("user.ser");
           ObjectInputStream in =
              new ObjectInputStream(new FileInputStream(inFile));
           User aUser = (User)in.readObject();
           System.out.println("User de-serialize from file is: "+aUser.toString());
           in.close();
        }
     }catch(Exception e) {
        System.out.println("Exception while serializing "+e.getMessage());
        e.printStackTrace();
     }
  }
}
