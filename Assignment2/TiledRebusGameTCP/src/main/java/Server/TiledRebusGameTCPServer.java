package Server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TiledRebusGameTCPServer {

    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            System.out.println(String.format("Improper command-line argument structure: %s\n" +
                    "\tShould be of the form: \"gradle runServer -Pport = <some port int>"));
            System.exit(0);
        }

        final Options cliOptions = new Options();
        final Option port = new Option("Pport", "port", true, "port number");
        port.setRequired(true);
        cliOptions.addOption(port);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine command;
        int parsedPort = 9000;
        try {
            command = parser.parse(cliOptions, args);
            parsedPort = Integer.parseInt(command.getOptionValue("port"));
        } catch (ParseException e) {
            e.printStackTrace();
            formatter.printHelp("utility-name", cliOptions);

            System.exit(1);
        }

        try (ServerSocket listener = new ServerSocket(parsedPort)) {
            while (true) {

            }
        } catch (IOException e) {
            System.out.println("Error while listening on socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final class Connection extends Thread {
        private static final int DELAY = 1_000;
        private final Socket clientSocket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

        public Connection(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                inputStream = new DataInputStream(clientSocket.getInputStream());
                outputStream = new DataOutputStream(clientSocket.getOutputStream());
                this.start();
            } catch (IOException e) {
                System.out.println("Connection setup failed: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                String receivedData = inputStream.readUTF();
                System.out.println("Data Received: " + receivedData);
                Thread.sleep(DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public DataInputStream getInputStream() {
            return inputStream;
        }

        public DataOutputStream getOutputStream() {
            return outputStream;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }
    }
}
