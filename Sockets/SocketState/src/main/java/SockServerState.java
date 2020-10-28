import java.net.*;
import java.io.*;
import java.util.*;

class SockServerState {
	public static void main (String args[]) throws Exception {
	    int count = 0;
	    ServerSocket    serv = null;
	    InputStream in = null;
	    OutputStream out = null;
	    Socket sock = null;
	    int port = 9099; // default port
	    int sleepDelay = 5; // default delay
	    int clientId = 0;
	    Map<Integer, Integer> totals = new HashMap<Integer, Integer>();
	    
	    if (args.length != 2) {
		System.out.println("Expected arguments <port(int)> <sleep-deplay(int)>");
		System.exit(1);
	    }
	    
	    try {
		port = Integer.parseInt(args[0]);
		sleepDelay = Integer.parseInt(args[1]);
	    } catch (NumberFormatException nfe) {
		System.out.println("[Port|sleepDelay] must be an integer");
		System.exit(2);
	    }
	    
	    try {
		serv = new ServerSocket(port);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    while (serv.isBound() && !serv.isClosed()) {
		System.out.println("SockServerState Ready...");
		try {
		    sock = serv.accept();
		    in = sock.getInputStream();
		    out = sock.getOutputStream();
		    
		    char c = (char)in.read();
		    System.out.print("Server received " + c);
		    Thread.sleep(sleepDelay);
		    switch (c) {
		    case 'r': 
			clientId = in.read();
			totals.put(clientId, 0);
			out.write(0);
			break;
		    case 't': 
			clientId = in.read();
			int x = in.read();
			System.out.print(" for client " + clientId + " " + x);
			Integer total = totals.get(clientId);
			if (total == null) {
			    total = 0;
			}
			totals.put(clientId, total + x);
			out.write(totals.get(clientId));
			break;
		    default:
			int x2 = in.read();
			int y = in.read();
			System.out.print(" " + x2 + " " + y);
			out.write(x2 + y);
		    }
		    System.out.println("");
		    out.flush();
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    if (out != null)  out.close();
		    if (in != null)   in.close();
		    if (sock != null) sock.close();
		}
	    }
	}
}

