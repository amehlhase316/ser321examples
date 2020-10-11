package server;

/**
 * Purpose: StudentCollection defines the interface to the server operations
 *
 * Ser321 Distributed Apps, and Ser423 Mobile Apps
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 */
public interface StudentCollection {
   public boolean saveToJsonFile();
   public boolean resetFromJsonFile();
   public boolean add(Student stud);
   public boolean remove(String aName);
   public Student get(String aName);
   public String getNameById(int id);
   public String[] getNames();
}
