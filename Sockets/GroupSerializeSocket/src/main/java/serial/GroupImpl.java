package serial;

import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;

/**

 * Purpose:
 * An interface defining operations for managing authentication groups.
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @file    GroupImpl.java
 * @date    August, 2020
 
 */
public class GroupImpl implements Group, Serializable {

  private String name;
  private Vector<User> users = new Vector<User>();

  public GroupImpl(String name) {
    this.name = name;
  }

  public String getName(){
    return name;
  }

  public void addUserToGroup(String id, String pwd) {
    boolean found = false;
    for (int i = 0; i<users.size(); i++) {
      if ((users.elementAt(i)).getId().equals(id))
        found = true;
    }
    if (!found)
      users.addElement(new User(id,pwd));
  }

  public Vector<String> getUserNames() {
    Vector<String> ret = new Vector<String>();
    for (Enumeration e = users.elements() ; e.hasMoreElements() ;) {
      ret.addElement(((User)e.nextElement()).getId());
    }
    return ret;
  }

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
}
