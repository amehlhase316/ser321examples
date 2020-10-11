package server;

import java.net.*;
import java.io.*;

/**
 * 
 * Purpose: A threaded server providing download service for the Serialized
 * Group The server waits for clients to connect and requiest to download the
 * file admin.ser (must execute: ant execute, prior to running server). The
 * clients requests the file by sending a "filetoclient^" string to the server.
 * The server responds by reading admin.ser and then sending the client a
 * message indicating how many bytes to expect in the transmission of the file.
 * The client responds OK, and the server sends the file's bytes. after the
 * client receives the proper number of bytes, it responds with a message OK.
 * For longer binary files, this same interaction/protocol could be used where
 * the client and server agree on a pre-specified buffer size for sending
 * successive parts of the file.
 *
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * 
 * @author Tim Lindquist (Tim.Lindquist@asu.edu) CIDSE - Software Engineering
 *         Ira Fulton Schools of Engineering, ASU Polytechnic
 * @version April, 2020
 * 
 */
public class GroupServer extends Thread {
  private static final boolean debugOn = true;
  private Socket conn;
  private int id;
  private int byteCount;

  public GroupServer(Socket aSock, int connId) {
    this.conn = aSock;
    this.id = connId;
    this.byteCount = 0;
  }

  public void run() {
    try {
      OutputStream outStream = conn.getOutputStream();
      InputStream inStream = conn.getInputStream();
      byte clientInput[] = socket.IO.read(inStream);
      long byteCount = 0;
      if (clientInput.length > 0) {
        String clientString = new String(clientInput);
        GroupServer.debug("Read from client number " + Integer.toString(id) + ", " + Integer.toString(clientInput.length)
            + " bytes as the string: " + clientString + "\n");
        if (clientString.equalsIgnoreCase("filetoclient^")) {
          System.out.println("Request to download group file");
          byteCount = this.downloadToClient();
          System.out.println("Download complete. Transferred " + byteCount + " bytes.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private long downloadToClient() {
    long byteCount = 0;
    try {
      // string request: filetoclient comes from client
      GroupServer.debug("Download file admin.ser to client");

      // get the connections streams
      OutputStream outStream = conn.getOutputStream();
      InputStream inStream = conn.getInputStream();

      // open the input file containing serialized group
      // NOTE: object stream serialization is compressed, not plain-text
      String filePath = "admin.ser";
      File inFile = new File(filePath);
      long len = inFile.length();

      // create a byte stream
      FileInputStream fis = new FileInputStream(inFile);

      // send the file to the client. First send the number of bytes
      // to be transferred.
      // start by reading the serialized group into buf to find how
      // many bytes
      byte[] buf = new byte[4096];
      int bufCount = fis.read(buf);
      GroupServer.debug("read " + bufCount + " bytes from the file");

      // send two integer byte counts: bufLengthThisRead^fileLength
      String thisBuf = String.valueOf(bufCount);
      String numStr = thisBuf + "^" + String.valueOf(len) + "^";
      GroupServer.debug("sending number of data bytes to client " + numStr);
      // send
      outStream.write(numStr.getBytes(), 0, numStr.getBytes().length);
      outStream.flush();

      // wait to be sure the client got it, by waiting for an OK.
      byte clientOK[] = socket.IO.read(inStream);
      GroupServer.debug("tried to read ok. Got " + clientOK.length + " bytes");
      String okStr = new String(clientOK);
      if (okStr.contains("OK")) {
        // if client got byte counts and replied OK then send buf
        outStream.write(buf, 0, bufCount);
        outStream.flush();
        GroupServer.debug("I sent bytes:" + Integer.toString(bufCount));
        byteCount = byteCount + bufCount;
      }

      // now, make sure the client got the buffer and replied OK
      byte gotIT[] = socket.IO.read(inStream);
      String gotStr = new String(gotIT);
      if (gotStr.contains("OK")) {
        GroupServer.debug("got OK");
      } else {
        GroupServer.debug("breaking because did not get OK. Got " + gotStr);
      }
      if (bufCount <= 0) {
        GroupServer.debug("sending Done to client");
        outStream.write("Done".getBytes(), 0, "Done".getBytes().length);
        outStream.flush();
      }
      fis.close();
      outStream.flush();
      outStream.close();
      conn.close();
    } catch (Exception e) {
      System.out.println("exception uploading to server: " + e.getMessage());
      e.printStackTrace();
    }
    return byteCount;
  }

  private static void debug(String message) {
    if (debugOn)
      System.out.println("debug: " + message);
  }

  /**
   * main method provides an infinte loop to accept connections from clients. when
   * a client connects, a new download thread is created to read the file and send
   * it to the client.
   */
  public static void main(String args[]) {
    ServerSocket serv;
    int connects = 0;
    Socket sock;
    int id = 0;
    int portNo = 3030;
    try {
      if (args.length != 1) {
        System.out.println("Expected Arguments: <port(int)>");
        System.exit(0);
      } else {
        portNo = Integer.parseInt(args[0]);
      }
      if (portNo <= 1024)
        portNo = 3030;
      serv = new ServerSocket(portNo);
      while (true) {
        System.out.println(
            "GroupServer waiting for client connect " + connects + " on port " + Integer.toString(portNo) + "\n");
        sock = serv.accept();
        connects = connects + 1;
        System.out.println("GroupServer connected to client: " + connects);
        GroupServer aServer = new GroupServer(sock, connects++);
        aServer.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
