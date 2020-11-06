package serial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

/**
 * Purpose: An interface defining operations for managing authentication groups.
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 *
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering Ira Fulton Schools of Engineering, ASU Polytechnic
 * @file GroupFileSerialize.java
 * @date April, 2020
 */
public class GroupFileSerialize {
    public static void main(String args[]) {

        try {
            Group admin = new GroupImpl("Administration");
            admin.addUserToGroup("Tim", "any");
            admin.addUserToGroup("Joe", "hisWord");
            admin.addUserToGroup("Sue", "herWord");

            System.out.println("server ready and waiting to export a group");

            File outFile = new File("admin.ser");
            ObjectOutputStream os =
                    new ObjectOutputStream(new FileOutputStream(outFile));
            os.writeObject(admin);
            os.flush();
            os.close(); //no need in Java to close the File outFiel (not a closeable object).
            System.out.println("server done exporting a group");

            File inFile = new File("admin.ser");
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(inFile));
            Group g = (GroupImpl) in.readObject();
            System.out.println("Group " + g.getName() + " deserialized. Includes:");
            Vector<String> users = g.getUserNames();
      /*
      for (Enumeration e = users.elements(); e.hasMoreElements() ;) {
        System.out.println((String)e.nextElement());
      }
      */
            for (String aUser : users) {
                System.out.println("User " + aUser);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
