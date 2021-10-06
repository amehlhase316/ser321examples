import java.net.*;
import java.util.List;
import java.util.Map;
import java.io.*;

/**
 * Class to show how to open a connection with a http URL and do a http request to get HeaderFields
 * and create an input stream that reads from this open connection
 * HttpURLConnection is a derived class  of URLConnection
 * which you can use when you need the extra API and you are dealing with HTTP or HTTPS only.
 */
public class SimpleGrabHttpURL {
	public static void main(String[] args) {
		if (args.length != 1) {
	    System.out.println("Expected Arguments: <url(String)>");
			System.exit(0);
		}

		HttpURLConnection conn = null;
		BufferedReader instream = null;
		try {
			// creates instance that represents a connection to the remote object referred to by the URL.
			conn = (HttpURLConnection) new URL(args[0]).openConnection();
			Map<String, List<String>> map = conn.getHeaderFields(); // Get header fields of specified URL
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				System.out.println("Key : " + entry.getKey() + 
		                 " ,Value : " + entry.getValue());
			}
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
				if (instream != null) instream.close(); // close input stream
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
