import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Purpose: Example client for temperature conversion and file uploading. A GUI window object extending JFrame. The GUI is for constructing
 * Extend this class and access its protected members to develop a Ser321WK3.Client. The user selects the method name from the combo box
 * drop down list, provides values for up to 2 arguments and then selects the <b>Call Method</b> button. The extending class handles the
 * button push, calls the appropriate method and places any result in the <b>resultTextFld</b>.
 * <p>
 * Ser321 Principles of Distributed Software Systems
 *
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering Ira Fulton Schools of Engineering, ASU Polytechnic
 * @version April, 2020
 */
public class ClientGui extends JFrame {

    private static final Font stdFont = new Font("SansSerif", Font.BOLD, 14);
    private static final Font entryFont = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font smlStdFont = new Font("SansSerif", Font.BOLD, 12);
    private static final Font smlEntryFont = new Font("SansSerif", Font.PLAIN, 12);
    /**
     * arg1TextFld is the JTextField containing the first argument
     */
    protected JTextField arg1TextFld;
    /**
     * arg2TextFld is the JTextField containing the first argument
     */
    protected JTextField arg2TextFld;
    /**
     * resultTextFld is the JTextField containing the result
     */
    protected JTextField resultTextFld;
    /**
     * functionCB is the JComboBox containing the method names implemented by the service.
     */
    protected JComboBox functionCB;
    /**
     * callFcnButt is the JButton for calling the selected function. The action command is "Call Method".
     */
    protected JButton callFcnButt;
    /**
     * exitButt is the JButton for completing the application. The action command is "Exit".
     */
    protected JButton exitButt;
    private Dimension dim;
    private Font useStd, useEntry;

    /**
     * Constructor for the function call client Gui. Creates a new GUI object with with appropriate title and correct method names in the
     * drop-down combo box. Sets the window to be visible.
     *
     * @param fcnNames a String array of the methods on the service that can be invoked.
     * @param title    a short title used to create a title line for the Gui window's title.
     */
    public ClientGui(String[] fcnNames, String title) {
        super("Ser321 " + title + " Temperature Conversion Ser321WK3.Client");
        Toolkit tk = Toolkit.getDefaultToolkit();
        dim = tk.getScreenSize();
        if (dim.width <= 1024) {
            useStd = smlStdFont;
            useEntry = smlEntryFont;
        } else {
            useStd = stdFont;
            useEntry = entryFont;
        }
        getContentPane().setFont(useStd);
        if (dim.width >= 1280) {
            setSize(560, 225);
        } else if (dim.width <= 1024) {
            setSize(500, 200);
        } else {
            setSize((int) (dim.width * .50), (int) (dim.height * .30));
        }
        setLocation(dim.width / 8, dim.height / 8);
        getContentPane().setLayout(new GridLayout(4, 1));

        JPanel pn = new JPanel();
        JLabel lab = new JLabel("Method");
        lab.setFont(useStd);
        pn.add(lab);
        functionCB = new JComboBox(fcnNames);
        functionCB.setFont(useEntry);
        pn.add(functionCB);
        getContentPane().add(pn);
        JPanel row = new JPanel();
        pn = new JPanel();
        lab = new JLabel("Temperature value to convert");
        lab.setFont(useStd);
        pn.add(lab);
        arg1TextFld = new JTextField(" ", 12);
        arg1TextFld.setFont(useEntry);
        pn.add(arg1TextFld);
        row.add(pn);
        getContentPane().add(row);

        pn = new JPanel();
        lab = new JLabel("Conversion Result");
        lab.setFont(useStd);
        pn.add(lab);
        resultTextFld = new JTextField(" ", 35);
        resultTextFld.setFont(useEntry);
        pn.add(resultTextFld);
        getContentPane().add(pn);

        pn = new JPanel();
        callFcnButt = new JButton("Call Method");
        callFcnButt.setFont(useStd);
        pn.add(callFcnButt);
        exitButt = new JButton("Exit");
        exitButt.setFont(useStd);
        pn.add(exitButt);
        getContentPane().add(pn);
        setVisible(true);
    }
}
