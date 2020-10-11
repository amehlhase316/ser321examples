/**

 * Purpose:
 * An class defining xml serializable user objects as java beans.
 * For a Java class to be XML serializable, it must have a parameterless
 * constructor and it must have get and set methods for each instance
 * variable in the class.
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @file    User.java
 * @date    January, 2020
 
 */
public class User {

    private String userId, userPwd, first, last;

    public User(){
    }

    public User(String id, String pwd, String first, String last) {
        this.userId = id;
        this.userPwd = pwd;
        this.first = first;
        this.last = last;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        userId = id;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String id) {
        userPwd = id;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String id) {
        first = id;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String id) {
        last = id;
    }

    public boolean check(String id, String pwd) {
        return (userId.equals(id) && userPwd.equals(pwd));
    }
}
