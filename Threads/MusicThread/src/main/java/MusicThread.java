import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.sound.sampled.*; //api classes to play wav file

/**

 * Purpose: demonstrate the use of a thread to provide interruptable background
 * playing of a wav file. To make this example work, there must be a wav
 * file in the project directory whose name matches the user-selected node
 * in the JTree. Select the tree node (example ComeMonday) then select
 * the Music-->Play menu item to play the file: ComeMonday.wav in the
 * project directory. Notice you can select a new node and then play to
 * move to a new song. Or, select Play again to restart from the beginning
 * of the current song.
 * You can generate a wav file for your an mp3 using the web site:
 *      http://audio.online-convert.com/convert-to-wav
 *
 * <p>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @version August, 2020
 */
public class MusicThread extends MusicLibraryGui implements
                                                 TreeWillExpandListener,
                                                 ActionListener,
                                                 TreeSelectionListener {
   private static final boolean debugOn = true;
   private PlayWavThread player = null;
   private boolean stopPlaying;

   public MusicThread(String base) {
      super(base);
      stopPlaying = false;
      for(int i=0; i<userMenuItems.length; i++){
         for(int j=0; j<userMenuItems[i].length; j++){
            userMenuItems[i][j].addActionListener(this);
         }
      }
      tree.addTreeSelectionListener(this);
      tree.addTreeWillExpandListener(this);
      setVisible(true);
   }

   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

   /**
    * a method to be called by music playing threads to determine
    * whether they should stop
    **/
   public boolean sezToStop(){
      return stopPlaying;
   }

   /**
    * create and initialize nodes in the JTree of the left pane.
    * buildInitialTree is called by MusicLibraryGui to initialize the JTree.
    * Classes that extend MusicLibraryGui should override this method to 
    * perform initialization actions specific to the extended class.
    * The default functionality is to set base as the label of root.
    * In your solution, you will probably want to initialize by deserializing
    * your library and building the tree.
    * @param root Is the root node of the tree to be initialized.
    * @param base Is the string that is the root node of the tree.
    */
   public void buildInitialTree(DefaultMutableTreeNode root, String base){
      try{
         System.out.println("buildInitialTree called by Gui constructor");
         // put some sample nodes in the tree so the user doesn't have
         // to select restore.
         initializeTree();
      }catch (Exception ex){
         JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
         ex.printStackTrace();
      }
   }

   public void initializeTree( ){
      tree.removeTreeSelectionListener(this);
      tree.removeTreeWillExpandListener(this);
      try{
         DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
         DefaultMutableTreeNode root =
            (DefaultMutableTreeNode)model.getRoot();
         String user = System.getProperty("user.name");
         System.out.println("user name is: "+user);
         //String sourceNames[] = {user,"Alone In Iz World","HanohanoCowboy",
         //                        "All The Greatest Hits","ComeMonday"};
	 File file = new File("./music");
	 File[] directories = file.listFiles(new FilenameFilter() {
		 @Override
		 public boolean accept(File current, String name) {
		     return new File(current, name).isDirectory();
		 }
	     });
         root.setUserObject("music");
	 DefaultMutableTreeNode dirNode = null;
	 DefaultMutableTreeNode wavNode = null;
	 for (int i = 0; i < directories.length; i++) {
	     // create a node for this directory
	     dirNode = new DefaultMutableTreeNode(directories[i].getName()); 
	     model.insertNodeInto(dirNode, root, model.getChildCount(root));
	     // within each directory find the .wav files
	     File[] wavFiles = directories[i].listFiles(new FilenameFilter() {
		     @Override
		     public boolean accept(File dir, String name) {
			 return name.endsWith(".wav");
		     }
		 });
	     for (int j = 0; j < wavFiles.length; j++) {
		 wavNode = new DefaultMutableTreeNode(wavFiles[j].getName().replaceFirst("[.][^.]+$", ""));
		 model.insertNodeInto(wavNode, dirNode, 0); //model.getChildCount(prevNode));
	     }
	 }
         // expand all the nodes in the JTree
         for(int r =0; r < tree.getRowCount(); r++){
            tree.expandRow(r);
         }
      }catch (Exception ex){
         JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
         ex.printStackTrace();
      }
      tree.addTreeSelectionListener(this);
      tree.addTreeWillExpandListener(this);
   }

   public void treeWillCollapse(TreeExpansionEvent tee) {
      tree.setSelectionPath(tee.getPath());
   }

   public void treeWillExpand(TreeExpansionEvent tee) {
      DefaultMutableTreeNode dmtn =
         (DefaultMutableTreeNode)tee.getPath().getLastPathComponent();
      System.out.println("will expand node: "+dmtn.getUserObject()+
                         " whose path is: "+tee.getPath());
   }

   public void valueChanged(TreeSelectionEvent e) {
      try{
         tree.removeTreeSelectionListener(this);
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();
	 if (node != null) {
	     String nodeLabel = (String)node.getUserObject();
	     titleJTF.setText(nodeLabel);
	 }
      }catch (Exception ex){
         ex.printStackTrace();
      }
      tree.addTreeSelectionListener(this);
   }

   public void actionPerformed(ActionEvent e) {
      if(e.getActionCommand().equals("Exit")) {
         System.exit(0);
      }else if(e.getActionCommand().equals("Play")) {
         try{
            System.out.println("Play Selected");
            // get the currently selected node in the tree.
            // if the user hasn't already selected a node for which
            // there must be a wav file then exit ungracefully!
            if(player != null && player.isAlive()){
               System.out.println("Already playing: Interrupting the thread");
               stopPlaying = true;
               Thread.sleep(500); // give the thread time to complete
               stopPlaying = false;
            }
	    String jTreeVarSelectedPath = "";
	    Object[] paths = tree.getSelectionPath().getPath();
	    for (int i=0; i<paths.length; i++) {
		jTreeVarSelectedPath += paths[i];
		if (i+1 <paths.length ) {
		    jTreeVarSelectedPath += File.separator;
		}
	    }
            player = new PlayWavThread(jTreeVarSelectedPath, this);
            player.start();
         }catch (InterruptedException ex){ // sleep may throw this exception
            System.out.println("MusicThread sleep was interrupted.");
            ex.printStackTrace();
         }
      } else if (e.getActionCommand().equals("Stop")) {
	  try {
	      if(player != null && player.isAlive()){
		  System.out.println("Already playing: Interrupting the thread");
		  stopPlaying = true;
		  Thread.sleep(500); // give the thread time to complete
		  stopPlaying = false;
	      }
	  } catch (InterruptedException ex){ // sleep may throw this exception
            System.out.println("MusicThread stop.");
            ex.printStackTrace();
         }
      } else {
	  System.out.println(e.getActionCommand() + " Selected");
      }
   }

   private void clearTree(DefaultMutableTreeNode root, DefaultTreeModel model){
      tree.removeTreeSelectionListener(this);
      tree.removeTreeWillExpandListener(this);
      try{
         DefaultMutableTreeNode next = null;
         int subs = model.getChildCount(root);
         for(int k=subs-1; k>=0; k--){
            next = (DefaultMutableTreeNode)model.getChild(root,k);
            debug("removing node labelled:"+(String)next.getUserObject());
            model.removeNodeFromParent(next);
         }
      }catch (Exception ex) {
         System.out.println("Exception while trying to clear tree:");
         ex.printStackTrace();
      }
      tree.addTreeSelectionListener(this);
      tree.addTreeWillExpandListener(this);
   }

   public static void main(String args[]) {
      try{
         String name = "Music Library";
         if (args.length >= 1) {
            name = args[0];
         }
         MusicThread ltree = new MusicThread(name);
      }catch (Exception ex){
         ex.printStackTrace();
      }
   }
}

/**
 *  A thread class to play a wav file. PlayWavThread opens an audio input
 * stream and plays it. To allow play to be interrupted, each time a new
 * buffer of the wav file is read, the thread checks with the server to see
 * whether it should complete. The server signals by setting and returning
 * a boolean value indicating that playing the wav file should stop.
 **/
class PlayWavThread extends Thread {
   private String aTitle;
   private MusicThread parent;
   public PlayWavThread(String aTitle, MusicThread parent) {
      this.parent = parent;
      this.aTitle = aTitle;
   }

   public void run (){
      int BUFFER_SIZE = 4096;
      AudioInputStream audioStream;
      AudioFormat audioFormat;
      SourceDataLine sourceLine;
      try{
         Thread.sleep(100); //wait 200 milliseconds before playing the file.
         System.out.println("Playing the wav file: " +aTitle);
         //String fn = (aTitle.startsWith("Han")) ? aTitle+".mp3" : aTitle+".wav";
         String fn = aTitle+".wav";
         //audioStream = AudioSystem.getAudioInputStream(new File(aTitle+".wav"));
         audioStream = AudioSystem.getAudioInputStream(new File(fn));
         audioFormat = audioStream.getFormat();
         DataLine.Info i = new DataLine.Info(SourceDataLine.class, audioFormat);
         sourceLine = (SourceDataLine) AudioSystem.getLine(i);
         sourceLine.open(audioFormat);
         sourceLine.start();
         int nBytesRead = 0;
         byte[] abData = new byte[BUFFER_SIZE];
         while(nBytesRead != -1){
            try{
               if(parent.sezToStop()){
                  System.out.println("Interrupted playing: "+aTitle);
                  break;
               }
               nBytesRead = audioStream.read(abData, 0, abData.length);
               if (nBytesRead >= 0) {
                  @SuppressWarnings("unused")
                     int nBytesWritten = sourceLine.write(abData,0,nBytesRead);
               }
            } catch (Exception e){
               e.printStackTrace();
            }
         }
         sourceLine.drain();
         sourceLine.close();
         audioStream.close();
      }catch (Exception e){
         e.printStackTrace();
      }
   }
}
