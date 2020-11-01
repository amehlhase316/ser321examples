package Ser321WK3.Client;

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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TiledRebusGameTCPClient {

    public static void main(String[] args) {
        Socket clientSocket = null;

        final Options cliOptions = new Options();

        final Option host = new Option("Phost", "host", true, "game server IP address");
        host.setRequired(true);
        final Option port = new Option("Pport", "port", true, "port : int (i.e. 9_000)");
        port.setRequired(true);

        cliOptions.addOption(host);
        cliOptions.addOption(port);

        final CommandLineParser parser = new DefaultParser();
        final HelpFormatter helpFormatter = new HelpFormatter();
        final CommandLine command;

        // Parse command line args into host:port.
        int parsedPort = 9000;
        String parsedIPAddress = "localhost";
        try {
            command = parser.parse(cliOptions, args);
            parsedPort = Integer.parseInt(command.getOptionValue(port.getOpt()));
            parsedIPAddress = command.getOptionValue(host.getOpt());
        } catch (ParseException e) {
            e.printStackTrace();
            helpFormatter.printHelp("utility-name", cliOptions);
            System.out.printf("\nImproper command-line argument structure: %s\n" +
                    "\tShould be of the form: \"gradle runClient -Pport = <some port int> -Phost = <some host IP address>%n", Arrays.toString(args));
            System.exit(1);
        }

        // Connect to the server.
        boolean gameOver = false;
        try {
            clientSocket = new Socket(parsedIPAddress, parsedPort);
            final DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            final DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            final ClientGui gameGui = new ClientGui();
            do {
                gameGui.show(true);
            } while (!gameOver);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
