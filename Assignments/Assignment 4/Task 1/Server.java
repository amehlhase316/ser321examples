import java.net.*;
import java.io.*;

class Server {
    public static void main(String args[]) throws Exception {

        StringList strings = new StringList();

        if (args.length != 1) {
            System.out.println("Usage: ThreadedServer <port>");
            System.exit(1);
        }
        
        ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));
        System.out.println("Server Started...");
        while (true) {
            System.out.println("Accepting a Request...");
            Socket sock = server.accept();

            Performer performer = new Performer(sock, strings);
            performer.doPerform();
        }
    }
}
