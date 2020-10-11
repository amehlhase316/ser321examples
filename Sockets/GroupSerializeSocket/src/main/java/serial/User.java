package serial;

import java.io.Serializable;

/**

 * Purpose:
 * An interface defining operations for managing authentication groups.
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @file    User.java
 * @date    April, 2020
 
 */
class User implements Serializable {

  private String userId, userPwd;

  protected User(String id, String pwd) {
    userId = id;
    userPwd = pwd;
  }

  protected String getId() {
    return userId;
  }

  protected boolean check(String id, String pwd) {
    return (userId.equals(id) && userPwd.equals(pwd));
  }
}
    
