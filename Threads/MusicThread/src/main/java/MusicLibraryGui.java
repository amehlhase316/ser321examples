import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import javax.swing.JTextField;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.JSplitPane;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;

public class MusicLibraryGui extends JFrame
{
    private static final Font stdFont;
    private static final Font entryFont;
    private static final Font smlStdFont;
    private static final Font smlEntryFont;
    private Dimension dim;
    private Font useStd;
    private Font useEntry;
    private JSplitPane displaySplitPane;
    protected JTree tree;
    protected String[] menuNames;
    protected String[] fileMenuNames;
    private static final int[] fileMnemonics;
    protected String[] musicMenuNames;
    private static final int[] musicMnemonics;
    protected JMenuItem[][] userMenuItems;
    protected String[][] menuItemNames;
    private int[][] mnemonics;
    protected JTextField titleJTF;
    
    public MusicLibraryGui(final String s) {
        super(s);
        this.menuNames = new String[] { "File", "Music" };
        this.fileMenuNames = new String[] { "Exit" };
        this.musicMenuNames = new String[] { "Play", "Stop" };
        this.userMenuItems = new JMenuItem[][] { new JMenuItem[this.fileMenuNames.length], new JMenuItem[this.musicMenuNames.length] };
        this.menuItemNames = new String[][] { this.fileMenuNames, this.musicMenuNames };
        this.mnemonics = new int[][] { MusicLibraryGui.fileMnemonics, MusicLibraryGui.musicMnemonics };
        this.dim = Toolkit.getDefaultToolkit().getScreenSize();
        if (this.dim.width < 1024) {
            this.useStd = MusicLibraryGui.smlStdFont;
            this.useEntry = MusicLibraryGui.smlEntryFont;
        }
        else {
            this.useStd = MusicLibraryGui.stdFont;
            this.useEntry = MusicLibraryGui.entryFont;
        }
        this.getContentPane().setFont(this.useStd);
        if (this.dim.width >= 1024) {
            this.setSize(500, 250);
        }
        else if (this.dim.width == 800) {
            this.setSize(330, 240);
        }
        else {
            this.setSize((int)(this.dim.width / 3.0), (int)(this.dim.height / 3.0));
        }
        this.setLocation(this.dim.width / 8, this.dim.height / 8);
        this.setJMenuBar(this.buildMenuBar());
        final JPanel panel = new JPanel(new FlowLayout());
        final JPanel newLeftComponent = new JPanel(new BorderLayout());
        final JLabel comp = new JLabel("Files");
        comp.setFont(this.useEntry);
        newLeftComponent.add(comp, "North");
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(s);
        this.tree = new JTree(new DefaultTreeModel(root));
        this.buildInitialTree(root, s);
        this.tree.setShowsRootHandles(true);
        this.tree.setEditable(true);
        newLeftComponent.add(new JScrollPane(this.tree), "Center");
        final JPanel view = new JPanel();
        view.add(this.titleJTF = new JTextField("Music Title", 15));
        (this.displaySplitPane = new JSplitPane(1, newLeftComponent, new JScrollPane(view))).setOneTouchExpandable(true);
        this.displaySplitPane.setDividerLocation((int)(this.dim.width / 7.5));
        this.getContentPane().add(this.displaySplitPane, "Center");
        this.setVisible(true);
    }
    
    private JMenuBar buildMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        for (int i = 0; i < this.menuNames.length; ++i) {
            final JMenu c = new JMenu(this.menuNames[i]);
            if (i == 0) {
                c.setMnemonic(70);
            }
            else {
                c.setMnemonic(77);
            }
            c.setFont(this.useEntry);
            for (int j = 0; j < this.userMenuItems[i].length; ++j) {
                (this.userMenuItems[i][j] = new JMenuItem(this.menuItemNames[i][j])).setMnemonic(this.mnemonics[i][j]);
                this.userMenuItems[i][j].setActionCommand(this.menuItemNames[i][j]);
                this.userMenuItems[i][j].setFont(this.useEntry);
                c.add(this.userMenuItems[i][j]);
            }
            menuBar.add(c);
        }
        return menuBar;
    }
    
    protected void buildInitialTree(final DefaultMutableTreeNode defaultMutableTreeNode, final String userObject) {
        defaultMutableTreeNode.setUserObject(userObject);
    }
    
    static {
        stdFont = new Font("SansSerif", 1, 12);
        entryFont = new Font("SansSerif", 0, 12);
        smlStdFont = new Font("SansSerif", 1, 9);
        smlEntryFont = new Font("SansSerif", 0, 9);
        fileMnemonics = new int[] { 83, 69 };
        musicMnemonics = new int[] { 65, 82, 80 };
    }
}
