package Assignment3Starter;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The picture grid
 * 
 * Note: This is a Moore state machine. Check the enum States to see the different modes it can be in. 
 *
 * Methods of interest
 * ----------------------
 * newGame(int dimension) - Reset the board and set grid size to dimension x dimension
 * insertImage(String fname, int row, int col) - Insert an image at (col, row)
 * 
 */
public class PicturePanel extends JPanel {
  // needed as JPanel is Serializable
  private static final long serialVersionUID = 1L;
  
  /**
   * Exception to throw when invalid coordinates are provided
   */
  public static class InvalidCoordinateException extends Exception {
    // Needed as Exception is Serializable
    private static final long serialVersionUID = 3L;
    
    public final int actualRows;
    public final int actualColumns;
    public final int attemptedRow;
    public final int attemptedColumn;
    
    public InvalidCoordinateException(int height, int width, int row, int column) {
      actualRows = height;
      actualColumns = width;
      attemptedRow = row;
      attemptedColumn = column;
    }
    
    @Override
    public String toString() {
      return "Board size is " + actualRows + "x" + actualColumns + ". Invalid coordinate: (" + attemptedRow + ", " + attemptedColumn + ")";
    }
  }

  // States that the widget can be in
  private enum States {
    // no game started
    NotStarted, 
    // game started, but has no image and therefore doesn't know size
    InGameNoImage, 
    // game fully initialized
    InGameWithImage
  }

  // picture grid state
  private JLabel[][] labels;
  private States state;

  /**
   * Constructor
   */
  public PicturePanel() {
    setLayout(new FlowLayout());
    setSize(500, 500);
    labels = new JLabel[0][0];

    state = States.NotStarted;
  }

  /**
   * Creates a new game 
   * NOTE: Will reset all state and clear board
   * @param dimension - size of rows and columns
   */
  public void newGame(int dimension) {
    // clear board
    this.removeAll();
    // set size of grid
    setLayout(new GridLayout(dimension, dimension));
    // initialize labels
    labels = new JLabel[dimension][dimension];
    for (int row = 0; row < dimension; ++row) {
      for (int col = 0; col < dimension; ++col) {
        labels[row][col] = new JLabel();
        // add to grid
        add(labels[row][col]);
      }
    }
    state = States.InGameNoImage;
  }

  /**
   * Utility method to set the dimensions of all containers
   * @param width of first image
   * @param height of first image
   */
  private void handleFirstImage(int width, int height) {
    if (state == States.InGameNoImage) {
      // calculate and set bounding box
      int totalDimensionWidth = labels.length * width;
      int totalDimensionHeight = labels.length * height;
      setSize(totalDimensionWidth, totalDimensionHeight);
      
      // set each images dimensions
      for (int row = 0; row < labels.length; ++row) {
        for (int col = 0; col < labels[0].length; ++col) {
          labels[row][col].setSize(width, height);
        }
      }
      state = States.InGameWithImage;
    }
  }
  
  /**
   * Insert an image at position at (col, row)
   * @param fname - filename of image to display
   * @param row - image box row
   * @param col - image box column
   * @return true if image was found and set, false otherwise
   * @throws IOException - File error
   * @throws InvalidCoordinateException - Invalid coordinate attempted
   */
  public boolean insertImage(String fname, int row, int col) throws IOException, InvalidCoordinateException {
    // Check or invalid coordinates
    if (row < 0 || col < 0 || 
        row >= 0 && labels.length <= row || 
        labels[row].length <= col) {
      throw new InvalidCoordinateException(labels.length, labels.length, row, col);
    }
    
    // create file reference
    File file = new File(fname);
    if (file.exists()) {
      // import image
      BufferedImage img = ImageIO.read(file);
      // create icon to display
      ImageIcon icon = new ImageIcon(img); 
      // do we need to setup the dimensions of all the containers?
      handleFirstImage(icon.getIconWidth(), icon.getIconHeight());
      // insert image
      labels[row][col].setIcon(icon);
      return true;
    }
    return false;
  }
}
