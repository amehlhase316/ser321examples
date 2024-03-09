import java.io.*;

/**
 * Purpose: demonstrate simple Java Fraction class with command line,
 * jdb debugging, and Ant build file.
 *
 * Ser321 Foundations of Distributed Applications
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version January 2020
 * 
 * Updated to accept command line arguments for numerator and denominator.
 */
public class Fraction {

   private int numerator, denominator;

   public Fraction() {
      numerator = denominator = 0;
   }

   public void print() {
      System.out.print(numerator + "/" + denominator);
   }

   public void setNumerator (int n) {
      numerator = n;
   }

   public void setDenominator (int d) {
      denominator = d;
   }

   public int getDenominator() {
      return denominator;
   }

   public int getNumerator() {
      return numerator;
   }

   /**
    * The main method now accepts two command line arguments to set the numerator
    * and denominator. If no arguments are provided, it defaults to 1/1.
    * If one argument is provided, it is used for the numerator with a default denominator of 1.
    */
   public static void main (String args[]) {
      try {
         Fraction frac = new Fraction();

         // Default values for numerator and denominator
         int num = 1;
         int denom = 1;

         // Set numerator if provided as the first argument
         if (args.length > 0) {
             num = Integer.parseInt(args[0]);
         }

         // Set denominator if provided as the second argument
         if (args.length > 1) {
             denom = Integer.parseInt(args[1]);
         }

         // Set the values in the Fraction instance
         frac.setNumerator(num);
         frac.setDenominator(denom);

         // Print the fraction to the console
         System.out.print("The fraction is: ");
         frac.print();
         System.out.println("");

      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
