package Ser321WK3.Client;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import Ser321WK3.Payload;

import static Ser321WK3.CustomTCPUtilities.parseInt;
import static Ser321WK3.CustomTCPUtilities.parsePayload;

public class TiledRebusGameTCPClient {

    public static void main(String[] args) {
        Socket clientSocket = null;

        // Parse command line args into host:port.
        int parsedPort = 0;
        String parsedIPAddress = "localhost";
        try {
            parsedPort = Integer.parseInt(args[0]);
            parsedIPAddress = args[1];
        } catch (Exception e) {
            try {
                parsedPort = Integer.parseInt(args[1]);
                parsedIPAddress = args[1];
            } catch (Exception exc) {
                exc.printStackTrace();
                System.out.printf("\nImproper command-line argument structure: %s\n" +
                        "\tShould be of the form: \"gradle runClient -Pport = <some port int> -Phost = <some host IP address>%n", Arrays.toString(args));
                System.exit(1);
            }
        }

        // Connect to the server.
        boolean gameOver = false;
        try {
            clientSocket = new Socket(parsedIPAddress, parsedPort);
            final DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            final DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            final ClientGui gameGui = new ClientGui();
            Payload gameSetupPayload = parsePayload(inputStream.readUTF());
            int gridDimension = initializeGame(gameGui, gameSetupPayload);
            outputStream.writeUTF(new Payload(Integer.toString(gridDimension), false, false).toString());
            do {
                Payload serverPayload = parsePayload(inputStream.readUTF());
                gameGui.outputPanel.appendOutput(serverPayload.getMessage());
                displayPayloadImages(serverPayload, gameGui, gridDimension);
                String userInput = gameGui.outputPanel.getInputText();
                outputStream.writeUTF(new Payload(userInput, false, false).toString());
                gameGui.show(false);
                serverPayload = parsePayload(inputStream.readUTF());
                displayPayloadImages(serverPayload, gameGui, gridDimension);
                gameGui.outputPanel.appendOutput(serverPayload.getMessage());
                gameGui.show(true);
                gameOver = serverPayload.gameOver();
            } while (!gameOver);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayPayloadImages(Payload serverPayload, ClientGui gameGui, int gridDimension) {
        if (serverPayload.getCroppedImages() != null && !serverPayload.getCroppedImages().isEmpty()) {
            int i = 0;
            int j = 0;
            for (BufferedImage image : serverPayload.getCroppedImages()) {
                if (i == gridDimension) {
                    break;
                }
                if (j == gridDimension) {
                    i++;
                    j = 0;
                }
                gameGui.insertImage(image, i, j++);
            }
        }
    }

    private static int initializeGame(ClientGui gameGui, Payload gameSetupPayload) {
        gameGui.outputPanel.appendOutput(gameSetupPayload.getMessage());
        gameGui.show(true);
        int gridDimension = gameSetup(gameGui);
        gameGui.show(false);
        gameGui.newGame(gridDimension);
        gameGui.show(true);
        return gridDimension;
    }

    private static int gameSetup(ClientGui gameGui) {
        int gridDimension;
        do {
            gridDimension = parseInt(gameGui.outputPanel.getInputText());
            if (gridDimension == 0) {
                gameGui.outputPanel.setInputText("");
                gameGui.outputPanel.appendOutput("Something went wrong. Please only enter an int >= 2.");
            }
        } while (gridDimension < 2);
        gameGui.outputPanel.setInputText("");
        return gridDimension;
    }
}
