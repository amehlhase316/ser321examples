import java.net.*;
import java.io.*;

/**
 * Class that shows how to create and open a connection with a URL
 * use an input stream to read from this open connection.
 */
public class SimpleGrabURL {
    public static void main(String[] args) {
	if (args.length != 1) {
	    System.out.println("Expected Arguments: <url(String)>");
	    System.exit(0);
	}

	URLConnection conn = null;
	BufferedReader instream = null;
	try {
	    URL url = new URL(args[0]);
		// creates instance that represents a connection to the remote object referred to by the URL.
	    conn = url.openConnection();
	    conn.connect();
		// input stream that reads from this open connection
	    instream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String line = null;
	    while ((line = instream.readLine()) != null) {
		System.out.println(line);
	    }
	}
	catch (IOException exc) {
	    exc.printStackTrace();
	}
	finally {
	    try {
		if (instream != null) instream.close();
	    }
	    catch (Throwable t) {
		t.printStackTrace();
	    }
	}
    }
}
