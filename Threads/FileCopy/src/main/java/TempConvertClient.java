import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**

 * Purpose:
 * Example threads to handle gui requests.
 * TempConvertClient extends ClientGui class to provide a
 * windowed client for Temperature Conversion.
 * This client demonstrates when using a separate thread may be useful
 * to avoid unnecessary waiting on the part of the user.
 * <p>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @version    April, 2020
 */
public class TempConvertClient extends ClientGui implements ActionListener {
  /**
   * static string array naming the methods that can be called.
   */
  private static String[] fcnNames = {"FahrenToCelsius", "CelsiusToFahren",
                                      "File Copy"};
  private CopyFile copyFile;
  /** TempConvertWS object */
  private TempConvert tc;
  /**
   * Constructor for the Temperature Conversion client. Calls the ClientGui
   * constructor to create and display the Window object, then adds action
   * listeners for the buttons.
   */
  public TempConvertClient(String[] args) {
    super(fcnNames, "Temperature Conversion");
    try {
      exitButt.addActionListener(this);
      callFcnButt.addActionListener(this);
      tc = new TempConvert();
      functionCB.setSelectedIndex(2);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Exception: " + ex.getMessage());
    }
  }
  /**
   * actionPerformed is defined by the ActionListener interface.
   * TempConvertClient registers itself to hear about action events caused by
   * the <b>Call Method</b> and <b>Exit</b> JButtons.  @param ActionEvent the
   * event object created by the source of the button push (the JButton object.)
   */
  public void
  actionPerformed(ActionEvent e) {
    try {
      if (e.getActionCommand().equals("Call Method")) {
        String methodName = (String)functionCB.getSelectedItem();
        double argument = 0.0;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        if (methodName.trim().equalsIgnoreCase("fahrenToCelsius")) {
          argument = Double.parseDouble(arg1TextFld.getText());
          resultTextFld.setText(arg1TextFld.getText() + " degrees "
                                + "Fahrenheit is " +
                                nf.format(tc.fahrenToCelsius(argument)) +
                                " degrees Celsius");
        } else if (methodName.trim().equalsIgnoreCase("CelsiusToFahren")) {
          argument = Double.parseDouble(arg1TextFld.getText());
          resultTextFld.setText(arg1TextFld.getText() + " degrees Celcius "
                                + "is " +
                                nf.format(tc.celsiusToFahren(argument)) +
                                " degrees Fahrenheit");
        } else {
          System.out.println("beginning the file copy");
          JFileChooser chooser = new JFileChooser();
          chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
          FileNameExtensionFilter filter =
              new FileNameExtensionFilter("jar files", "jar");
          chooser.setFileFilter(filter);
          int returnVal = chooser.showOpenDialog(this);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You selected the file: " +
                               chooser.getSelectedFile().getName());
            copyFile = new CopyFile(chooser.getSelectedFile());
            copyFile.start();
          }
        }
      } else if (e.getActionCommand().equals("Exit")) {
        System.exit(0);
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Exception: " + ex.getMessage());
    }
  }

  /**
   * main method for the client. All I do is call the constructor.
   */
  public static void main(String args[]) {
    TempConvertClient tcc = new TempConvertClient(args);
  }
}

class CopyFile extends Thread {
  private File aFile;
  public CopyFile(File aFile) { this.aFile = aFile; }
  public void run() {
    try {
      Thread.sleep(1000); // wait 1000 milliseconds before starting the copy.
      System.out.println("Opening the file: " + aFile.getName());
      String fileName = aFile.getName();
      String extension = fileName.substring(fileName.lastIndexOf("."));
      FileOutputStream fos = new FileOutputStream("tmp" + extension);
      System.out.println("Writing the file: tmp" + extension);
      FileInputStream fis = new FileInputStream(aFile);
      byte[] buf = new byte[4096];
      int soFar = 0;
      while (true) {
        int n = fis.read(buf);
        if (n < 0)
          break;
        fos.write(buf, 0, n);
        soFar = soFar + n;
        System.out.println("written " + soFar + " of " + aFile.length() +
                           " bytes");
        Thread.sleep(1000); // wait 2 seconds.
      }
      fis.close();
      fos.close();
      System.out.println("Completed the file copy.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
