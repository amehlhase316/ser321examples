package Ser321WK3.Client;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import Ser321WK3.Payload;

import static Ser321WK3.CustomTCPUtilities.parseInt;
import static Ser321WK3.CustomTCPUtilities.parsePayload;
import static Ser321WK3.CustomTCPUtilities.setReceivedData;
import static Ser321WK3.CustomTCPUtilities.waitForData;

public class TiledRebusGameTCPClient {

    public static final String GAME_INITIALIZATION_ERROR_MESSAGE = "Something went wrong. Please only enter an int >= 2.";

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
                parsedIPAddress = args[0];
            } catch (Exception exc) {
                exc.printStackTrace();
                System.out.printf("\nImproper command-line argument structure: %s\n" +
                        "\tShould be of the form: \"gradle runClient -Pport = <some port int> -Phost = <some host IP address>%n", Arrays.toString(args));
                System.exit(1);
            }
        }

        // Connect to the server.
        boolean gameOver;
        try {
            clientSocket = new Socket(parsedIPAddress, parsedPort);
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            final ClientGui gameGui = new ClientGui();
            final AtomicReference<String> receivedDataString = new AtomicReference<>("");
            waitForDataFromServer(inputStream, receivedDataString, null, 10);
            System.out.printf("%nData received from the server: %s%n", receivedDataString.get());
            Payload gameSetupPayload = parsePayload(receivedDataString.get());
            int gridDimension = initializeGame(gameGui, gameSetupPayload);
            outputStream.writeUTF(new Payload(null, Integer.toString(gridDimension), false, false).toString());
            outputStream.flush();
            setReceivedData(receivedDataString, "");

            do {
                waitForDataFromServer(inputStream, receivedDataString, gameGui, 10);
                System.out.printf("%nData received from the server: %s%n", receivedDataString.get());
                Payload serverPayload = parsePayload(receivedDataString.get());
                setReceivedData(receivedDataString, "");

                displayPayloadImages(serverPayload, gameGui, gridDimension);
                gameGui.outputPanel.appendOutput(serverPayload.getMessage());

                do {
                    waitForData(null, gameGui, receivedDataString, 60);
                } while (receivedDataString.get().isEmpty());

                System.out.printf("%nUser input being sent to the Server: %s%n", receivedDataString.get());
                outputStream.writeUTF(new Payload(null, receivedDataString.get(), false, false).toString());
                setReceivedData(receivedDataString, "");

                do {
                    waitForData(inputStream, null, receivedDataString, 10);
                } while (receivedDataString.get().isEmpty());

                serverPayload = parsePayload(receivedDataString.get());
                displayPayloadImages(serverPayload, gameGui, gridDimension);
                gameGui.outputPanel.appendOutput(serverPayload.getMessage());
                gameOver = serverPayload.gameOver();
            } while (!gameOver);

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException | NullPointerException e) {
                /*IGNORED*/
            }
        }
    }

    private static void waitForDataFromServer(DataInputStream inputStream, AtomicReference<String> receivedDataString,
                                              ClientGui gameGui, int timeToWait) throws IOException {
        do {
            try {
                waitForData(inputStream, gameGui, receivedDataString, timeToWait);
            } catch (Exception e) {
                /*IGNORE*/
            }
        } while (receivedDataString.get().isEmpty());
    }

    private static int initializeGame(ClientGui gameGui, Payload gameSetupPayload) {
        gameGui.outputPanel.appendOutput(gameSetupPayload.getMessage());
        gameGui.show(false);
        AtomicReference<String> gridDimension = new AtomicReference<>("");
        int returnValue;
        do {
            try {
                waitForData(null, gameGui, gridDimension, 20);
            } catch (Exception e) {
                gameGui.outputPanel.appendOutput(GAME_INITIALIZATION_ERROR_MESSAGE);
            }
            returnValue = parseInt(gridDimension.get());
            if (returnValue < 2) {
                gameGui.outputPanel.setInputText("");
                gameGui.outputPanel.appendOutput(GAME_INITIALIZATION_ERROR_MESSAGE);
            }
        } while (returnValue < 2);
        gameGui.newGame(returnValue);
        return returnValue;
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

}
