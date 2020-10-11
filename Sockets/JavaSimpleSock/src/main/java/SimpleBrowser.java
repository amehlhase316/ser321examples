import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

/**
 * A class to demonstrate Java's support for http socket connections. Ser321
 * Foundations of Distributed Software Systems see
 * 
 * @author Tim Lindquist Tim.Lindquist@asu.edu Software Engineering, CIDSE,
 *         IAFSE, ASU Poly
 * @version April 2020
 * 
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 */
public class SimpleBrowser extends JFrame implements ActionListener {

  private final static String home = "https://devhints.io/bash";
  private JTextField urlStrField;
  JEditorPane displayPane;

  /**
   * Setup the browser window
   * @param urlString initial page URL to load
   */
  public SimpleBrowser(String urlString) {
    
    /// Top Menu Bar ///
    
    // set up a file menu with one submenu and an exit menuItem
    JMenuBar menuBar = new JMenuBar();

    // setup File option in menubar
    JMenu menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_F);
    menu.setToolTipText("Use file menu to display new page or exit");
    
    // setup entries in File option
    String menuIs[] = { "Display", "Exit" };
    for (var label : menuIs) {
      var mi = new JMenuItem(label);
      mi.addActionListener(this);
      menu.add(mi);
    }
    // add File open to menuBar
    menuBar.add(menu);
    //add menuBar to window
    setJMenuBar(menuBar);
    
    /// Main Page Display ///
    
    // setup the page display
    displayPane = new JEditorPane();
    displayPane.setEditable(false);
    // give the page display scrollbars
    JScrollPane sourceScrollPane = new JScrollPane(displayPane);
    
    // add page display to the middle area of the JFrame(main GUI window)
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(sourceScrollPane, BorderLayout.CENTER);

    /// Bottom Menu Panel ///
    
    // setup URL input bar's panel
    JPanel bottomInputPanel = new JPanel();
    // create the go-to button
    JButton up = new JButton("Display");
    up.addActionListener(this);
    // add button to bar
    bottomInputPanel.add(up);
    
    // set default url if one isn't already there
    String defaultPage = home;
    if (!urlString.equals("")) {
      defaultPage = urlString;
    }
    
    // create the url input field for the bottom bar
    urlStrField = new JTextField(defaultPage, 45);
    // add url input field to bottom panel
    bottomInputPanel.add(urlStrField);
    // add panel to page layout
    getContentPane().add(bottomInputPanel, BorderLayout.SOUTH);
    
    // go to the url, error if not found
    goToUrl(defaultPage);
  }
  
  /**
   * Will navigate to the page
   * @param url to navigate to
   * @return true if successful, false otherwise
   */
  public boolean goToUrl(String url) {
    try {
      displayPane.setPage(new URL(url));
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "URL unreachable " + e.getMessage());
      return false;
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("Exit")) {
      System.exit(0);
    } else if (e.getActionCommand().equals("Display")) {
      goToUrl(urlStrField.getText());
    }
  }

  public static void main(String args[]) {
    String inStr = "";
    if (args.length >= 1) {
      inStr = args[0];
    }
    SimpleBrowser window = new SimpleBrowser(inStr);
    window.setSize(600, 600);
    window.setVisible(true);
  }
}
