package server;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * A class for TCP client-server connections with a threaded server that
 * implements JsonRPC method calls for a collection of Students.
 *
 * Ser321 Foundations of Distributed Software Systems
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 */
public class StudentCollectionTCPJsonRPCServer extends Thread {
   private Socket conn;
   private int id;
   private StudentCollectionSkeleton skeleton;

   public StudentCollectionTCPJsonRPCServer (Socket sock, int id,
                                             StudentCollection stdCol) {
      this.conn = sock;
      this.id = id;
      skeleton = new StudentCollectionSkeleton(stdCol);
   }

   public void run() {
      // setup connection
      try {
         OutputStream outSock = conn.getOutputStream();
         InputStream inSock = conn.getInputStream();
         byte clientInput[] = new byte[1024]; // up to 1024 bytes in a message.
         
         // receive request
         int numr = inSock.read(clientInput,0,1024);
         if (numr != -1) {
            //System.out.println("read "+numr+" bytes");
            String request = new String(clientInput,0,numr);
            System.out.println("request is: "+request);

            // prep and send response
            String response = skeleton.callMethod(request);
            byte clientOut[] = response.getBytes();
	         outSock.write(clientOut,0,clientOut.length);
            System.out.println("response is: "+response);
         }
         inSock.close();
         outSock.close();
         conn.close();
      } catch (IOException e) {
         System.out.println("I/O exception occurred for the connection:\n"+e.getMessage());
      }
   }
    
   public static void main (String args[]) {
      Socket sock;
      StudentCollection studCol = new StudentCollectionImpl();
      int id=0;
      try {
         if (args.length != 1) {
            System.out.println("Expected Arguments: <port(int)>");
            System.exit(0);
         }
         int portNo = Integer.parseInt(args[0]);
         if (portNo <= 1024) portNo=8888;
         ServerSocket serv = new ServerSocket(portNo);
         // accept client requests. For each request create a new thread to handle
         while (true) { 
            System.out.println("Student server waiting for connects on port "
                               +portNo);
            sock = serv.accept();
            System.out.println("Student server connected to client: "+id);
            StudentCollectionTCPJsonRPCServer myServerThread =
               new StudentCollectionTCPJsonRPCServer(sock,id++,studCol);
            myServerThread.start();
         }
      } catch(Exception e) {e.printStackTrace();}
   }
}
