import java.util.Vector;

/**
 * Purpose:
 * An interface defining operations for managing authentication groups.
 *
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * file    Group.java
 * @version    January, 2020
 */
public interface Group {

   /**
    * Serialize the users in the group to the file users.ser
    */
   public void saveToFile();

  /**
   * Get the name string associated with this authorization group.
   * Use getName to retrieve the name string property for the group.
   * @return The group's name string.
   */
  public String getName();

  /**
   * Associate a new user with this authorization group.
   * Use addUserToGroup to allow a new user access to group resources.
   * @param id Is a String specifying userId to add
   * @param pwd Is a String specifying password for user.
   */
  public void addUserToGroup(String id, String pwd);

  /**
   * Get the name strings of all users associated in this authorization group.
   * Use getName to retrieve the name string property for the group.
   * @return The vector of userId strings.
   */
  public Vector<String> getUserNames();

  /**
   * Determine whether a user is in the group.
   * Use isMember to authenticate a user password combination for this group
   * membership.
   * @param id Is a String specifying userId that is not already in the group
   * @param pwd Is a String specifying a password for id.
   * @return true if user and pwd are authorized;
   * otherwise return false.
   */
  public boolean isMember(String id, String pwd);

}
