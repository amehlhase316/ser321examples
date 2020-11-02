package Ser321WK3.Client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import Ser321WK3.Payload;

import static Ser321WK3.CustomTCPUtilities.parseInt;
import static Ser321WK3.CustomTCPUtilities.setReceivedData;
import static Ser321WK3.CustomTCPUtilities.waitForData;
import static Ser321WK3.CustomTCPUtilities.writePayloadOut;

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
            final AtomicReference<Payload> payloadAtomicReference = new AtomicReference<>(null);
            waitForDataFromServer(inputStream, payloadAtomicReference, null, 10);
            System.out.printf("%nData received from the server: %s%n", payloadAtomicReference.get());
            int gridDimension = initializeGame(gameGui, payloadAtomicReference.get());
            writePayloadOut(new Payload(null, Integer.toString(gridDimension), false, false), outputStream);
            outputStream.flush();
            setReceivedData(payloadAtomicReference, null);

            do {
                waitForDataFromServer(inputStream, payloadAtomicReference, gameGui, 10);
                System.out.printf("%nData received from the server: %s%n", payloadAtomicReference.get());

                displayPayloadImages(payloadAtomicReference.get(), gameGui, gridDimension);
                gameGui.outputPanel.appendOutput(payloadAtomicReference.get().getMessage());
                setReceivedData(payloadAtomicReference, null);

                do {
                    waitForData(null, gameGui, payloadAtomicReference, 60);
                } while (payloadAtomicReference.get() == null);

                System.out.printf("%nUser input being sent to the Server: %s%n", payloadAtomicReference.get());
                writePayloadOut(payloadAtomicReference.get(), outputStream);
                setReceivedData(payloadAtomicReference, null);

                do {
                    waitForData(inputStream, null, payloadAtomicReference, 10);
                } while (payloadAtomicReference.get() == null);

                System.out.printf("%nUser input being sent to the Server: %s%n", payloadAtomicReference.get());
                displayPayloadImages(payloadAtomicReference.get(), gameGui, gridDimension);
                gameGui.outputPanel.appendOutput(payloadAtomicReference.get().getMessage());
                gameOver = payloadAtomicReference.get().gameOver();
                setReceivedData(payloadAtomicReference, null);
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

    private static void waitForDataFromServer(DataInputStream inputStream, AtomicReference<Payload> payloadAtomicReference,
                                              ClientGui gameGui, int timeToWait) throws IOException {
        do {
            try {
                waitForData(inputStream, gameGui, payloadAtomicReference, timeToWait);
            } catch (Exception e) {
                /*IGNORE*/
            }
        } while (payloadAtomicReference.get() == null);
    }

    private static int initializeGame(ClientGui gameGui, Payload gameSetupPayload) {
        gameGui.outputPanel.appendOutput(gameSetupPayload.getMessage());
        gameGui.show(false);
        AtomicReference<Payload> gridDimension = new AtomicReference<>(null);
        int returnValue;
        do {
            try {
                waitForData(null, gameGui, gridDimension, 20);
            } catch (Exception e) {
                gameGui.outputPanel.appendOutput(GAME_INITIALIZATION_ERROR_MESSAGE);
            }
            returnValue = parseInt(gridDimension.get().getMessage());
            if (returnValue < 2) {
                gameGui.outputPanel.setInputText("");
                gameGui.outputPanel.appendOutput(GAME_INITIALIZATION_ERROR_MESSAGE);
            }
        } while (returnValue < 2);
        gameGui.newGame(returnValue);
        return returnValue;
    }

    private static void displayPayloadImages(Payload serverPayload, ClientGui gameGui, int gridDimension) throws IOException {
        if (serverPayload.getCroppedImages() != null && !serverPayload.getCroppedImages().isEmpty()) {
            int i = 0;
            int j = 0;
            for (byte[] imageBytes : serverPayload.getCroppedImages()) {
                BufferedImage image = null;
                if (i == gridDimension) {
                    break;
                }
                if (j == gridDimension) {
                    i++;
                    j = 0;
                }
                image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                gameGui.insertImage(image, i, j++);
            }
        }
    }

}
