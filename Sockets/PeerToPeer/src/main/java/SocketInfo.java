import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


/**
 * This is just a class that holds the host and port for a peer. Just for easier data handling
 * 
 */

public class SocketInfo {
	private String host;
	private int port;

	public SocketInfo (String host, int port){
		this.host = host;
		this.port = port;
	}

	public int getPort(){
		return port;
	}

	public String getHost(){
		return host;
	}

}