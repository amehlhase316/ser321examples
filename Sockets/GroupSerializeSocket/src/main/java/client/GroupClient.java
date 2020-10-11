package client;

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import serial.GroupImpl;
import serial.Group;

/**

 * Purpose:
 * A threaded server providing download service for the Serialized Group
 * The server waits for clients to connect and requiest to download the
 * file admin.ser (must execute: ant execute, prior to running server).
 * The clients requests the file by sending a "filetoclient^" string to
 * the server. The server responds by reading admin.ser and then sending the
 * client a message indicating how many bytes to expect in the transmission of
 * the file. The client responds OK, and the server sends the file's bytes.
 * after the client receives the proper number of bytes, it responds with a 
 * message OK. For longer binary files, this same interaction/protocol could
 * be used where the client and server agree on a pre-specified buffer size
 * for sending successive parts of the file.
 *
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *                       Ira Fulton Schools of Engineering, ASU Polytechnic
 * @version April, 2020
 
 */
public class GroupClient extends Object {

   private static final byte[] fileToClientBytes = "fileToClient^".getBytes();
   private String serverHost;
   private int aPort;
   private String filename;  //eg. admin2.ser

   private static final boolean debugOn = true;

   public GroupClient(String serverHost, int portToUse, String fPath){
      this.serverHost = serverHost;
      this.aPort = portToUse;
      this.filename = fPath;
   }

   public int downloadGroup(){
      int ret = 0;
      byte[] okBytes = "OK".getBytes();
      Socket sock = null;
      InputStream inStream = null;
      OutputStream outStream = null;
      FileOutputStream fos = null;
      try{
         System.out.println("Connecting to GroupServer: "+serverHost+
                            ":"+aPort+" to receive serialized group.");
         sock = new Socket(serverHost, aPort);
         inStream = sock.getInputStream();
         outStream = sock.getOutputStream();
         
         // send GroupServer the string: fileToClient^
         debug("sending "+fileToClientBytes.length+" bytes in the string: "+
                 (new String(fileToClientBytes,0,fileToClientBytes.length)));
         outStream.write(fileToClientBytes,0,fileToClientBytes.length);
         outStream.flush();
         
         // create file output stream for writing the serialized group
         fos = new FileOutputStream(filename);
         
         // read the number of bytes in buffer followed by total file bytes
         byte[] bufCount = socket.IO.read(inStream);
         int byteCount = 1;
         String packet;
         int packetLength = 0;
         if(bufCount.length > 0){
            // process packet length header packet
            packet = new String(bufCount, 0, bufCount.length);
            debug("read byte counts from server: "+packet);
            StringTokenizer st = new StringTokenizer(packet, "^");
            packetLength = Integer.parseInt(st.nextToken());
            byteCount = Integer.parseInt(st.nextToken());

            // signal to send
            outStream.write(okBytes,0,okBytes.length);
            debug("wrote OK, next looking for "+packetLength+" bytes");

            // read the bytes from the network
            byte [] buf = socket.IO.read(inStream);
            // verify
            if (buf.length != packetLength) {
               debug("unexpected read buffer got "+buf.length +
                     " expected "+packetLength+" bytes.");
            }
            // write to the local file.
            fos.write(buf,0,packetLength);
            outStream.write(okBytes,0,okBytes.length);
            outStream.flush();
         }

         // should send OK response, but close works ok to.

         ret = byteCount;
         fos.close();
         inStream.close();
         sock.close();
         System.out.println("Finished downloading serialized group with "+
                            byteCount+" bytes.");
      }catch (Exception e){
         e.printStackTrace();
      }
      return ret;
   }

   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

   public static void main(String args[]){
      try{
         int portNo = 3030;
         String host = "127.0.0.1";
         if (args.length != 2) {
            System.out.println(
               "Expected Arguments: <host(String)> <port(int)>");
            System.exit(0);
         }else{
            host = args[0];
            portNo = Integer.parseInt(args[1]);
         }
         GroupClient gc = new GroupClient(host,portNo,"admin2.ser");
         int howManyBytes = 0;
         howManyBytes = gc.downloadGroup();
         gc.debug("Completed download, transferred "+ howManyBytes +" bytes.");
         File inFile = new File("admin2.ser");
         ObjectInputStream in =
            new ObjectInputStream(new FileInputStream(inFile));
         Group g = (GroupImpl)in.readObject();
         System.out.println("Group "+g.getName()+" deserialized. Includes:");
         Vector<String> users = g.getUserNames();
         for (String aUser : users){
            System.out.println("User "+aUser);
         }
         in.close();
      }catch (Exception e){
         System.out.println("Exception in transferring group: "+e.getMessage());
         e.printStackTrace();
      }
   }
   
}
