/**

 * Purpose:
 * Demonstrate threads in UI's. This sample application uploads files
 * and converts temperature between Celsius and Fahrenheit.
 * <p>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @version    April, 2020
 */
public class TempConvert {

   /**
     * Parameterless constructor for the class that initializes the
     * number of calls to zero.
     */
   public TempConvert() {
   }

   /**
    * Caluclate and return the Celsius equivalent of the argument.
    * @param dFahrenheit The double value of Fahrenheit degrees.
    * @return The corresponding degrees in celsius as a double value.
    */
   public double fahrenToCelsius(double dFahrenheit) {
      return ((dFahrenheit - 32) * 5) / 9;
   }

   /**
    * Caluclate and return the Fahrenheit equivalent of the argument.
    * @param celsius The double value of degrees in Celsius
    * @return The corresponding degrees in Fahrenheit as a double value.
    */
   public double celsiusToFahren(double celsius) {
      return 32 + (( celsius * 9) / 5);
   }

}
