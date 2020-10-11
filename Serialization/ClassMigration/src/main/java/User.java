import java.io.Serializable;

/**
 * Purpose: A Class demonstrating Java support for class migration. A User
 * object that is serialized to a disk file and then de-serialized after the
 * class has been modified generates an exception without definition of the
 * appropriate serialVersionUID serialver -classpath build/classes/java/main
 * User define the resulting output in the class before changes.
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * 
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *         Ira Fulton Schools of Engineering, ASU Polytechnic
 * @file User.java
 * @date January, 2020
 */
class User implements Serializable {
  // https://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
  // private static final long serialVersionUID = 1L;

  private String userId, userPwd;
  //private int age;

  public User(String id, String pwd) {
    userId = id;
    userPwd = pwd;
    //age = 30;
  }

  public String getId() {
    return userId;
  }

  public String toString() {
    String ret;
    ret = "User with id: "+userId+" has password: "+userPwd;
    //ret = "User with id: "+userId+" has password: "+userPwd +" age: "+age;
    return ret;
  }
}
