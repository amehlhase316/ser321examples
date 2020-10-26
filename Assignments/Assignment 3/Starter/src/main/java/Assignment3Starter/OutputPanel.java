package Assignment3Starter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * The output panel that includes an input box, a submit button, and an output
 * text area.
 * 
 * Methods of interest
 * ----------------------
 * getInputText() - Get the input text box text
 * setInputText(String newText) - Set the input text box text
 * addEventHandlers(EventHandlers handlerObj) - Add event listeners
 * appendOutput(String message) - Add message to output text
 */
public class OutputPanel extends JPanel {
  // Needed because JPanel is Serializable
  private static final long serialVersionUID = 2L;

  /**
   * Generic event handler for events generated in the panel GUI
   * 
   * Uses Observer pattern
   */
  public interface EventHandlers {
    // Executes for every key press in the input textbox
    void inputUpdated(String input);
    
    // executes when the submit button is clicked
    void submitClicked();
  }

  private JTextField input;
  private JButton submit;
  private JTextArea area;
  private ArrayList<EventHandlers> handlers = new ArrayList<>();

  /**
   * Constructor
   */
  public OutputPanel() {
    setLayout(new GridBagLayout());

    // Setup input text box
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.75;
    input = new JTextField();
    input.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        JTextField textField = (JTextField) e.getSource();
        for (var handler : handlers) {
          handler.inputUpdated(textField.getText());
        }
      }
    });
    add(input, c);

    // Setup submit button
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    submit = new JButton("Submit");
    submit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == submit) {
          for (var handler : handlers) {
            handler.submitClicked();
          }
        }
      }
    });
    add(submit, c);

    // Setup scrollable output text area
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.weighty = 0.75;
    area = new JTextArea();
    JScrollPane pane = new JScrollPane(area);
    add(pane, c);
  }

  /**
   * Get input text box text
   * @return input box value
   */
  public String getInputText() {
    return input.getText();
  }

  /**
   * Set input text box text
   * @param newText the text to put in the text box
   */
  public void setInputText(String newText) {
    input.setText(newText);
  }

  /**
   * Register event observers
   * @param handler
   */
  public void addEventHandlers(EventHandlers handlerObj) {
    handlers.add(handlerObj);
  }
  
  /**
   * Append a message to the output panel
   * @param message - the message to print
   */
  public void appendOutput(String message) {
    area.append(message + "\n");
  }
}
