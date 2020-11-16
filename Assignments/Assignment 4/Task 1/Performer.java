import java.net.*;
import java.io.*;

class Performer {

    StringList  state;
    Socket      sock;

    public Performer(Socket sock, StringList strings) {
        this.sock = sock;    
        this.state = strings;
    }

    public void doPerform() {
        
        BufferedReader in = null;
        PrintWriter out = null;
        try {

            in = new BufferedReader(
                        new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            out.println("Enter text (. to disconnect):");

            boolean done = false;
            while (!done) {
                String str = in.readLine();

                if (str == null || str.equals("."))
                    done = true;
                else {
                    state.add(str);
                    out.println("Server state is now: " + state.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
            try {
                in.close();
            } catch (IOException e) {e.printStackTrace();}
            try {
                sock.close();
            } catch (IOException e) {e.printStackTrace();}
        }
    }
}

