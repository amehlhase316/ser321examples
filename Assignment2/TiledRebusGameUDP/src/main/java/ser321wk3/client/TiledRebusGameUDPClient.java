package ser321wk3.client;

import org.awaitility.Awaitility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import ser321wk3.CustomProtocol;
import ser321wk3.CustomProtocolHeader;
import ser321wk3.Payload;

import static ser321wk3.CustomUDPUtilities.BYTE_ARRAY_SIZE;
import static ser321wk3.CustomUDPUtilities.convertBase64encodedStringToBufferedImage;
import static ser321wk3.CustomUDPUtilities.parseInt;
import static ser321wk3.CustomUDPUtilities.readCustomProtocol;
import static ser321wk3.CustomUDPUtilities.writeCustomProtocolOut;

public class TiledRebusGameUDPClient {

    public static final String GAME_INITIALIZATION_ERROR_MESSAGE = "Something went wrong. Please only enter an int >= 2.";
    private static final Logger LOGGER = Logger.getLogger(TiledRebusGameUDPClient.class.getName());
    private static final ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_ARRAY_SIZE);
    private static final AtomicReference<InetAddress> address = new AtomicReference<>(null);
    private static final AtomicInteger port = new AtomicInteger(0);
    private static int gridDimension;
    private static int numberOfCorrectResponses;
    private static boolean gameOver;
    private static ClientGui gameGui;
    private static DatagramSocket udpSocket;

    public static void main(String[] args) throws IOException, InterruptedException {

        // Parse command line args into host:port.
        int parsedPort = 0;
        InetAddress parsedAddress = null;
        try {
            parsedPort = Integer.parseInt(args[0]);
            parsedAddress = InetAddress.getByName(args[1]);
        } catch (Exception e) {
            try {
                parsedPort = parseInt(args[1]);
                parsedAddress = InetAddress.getByName(args[0]);
            } catch (Exception exc) {
                exc.printStackTrace();
                LOGGER.log(Level.SEVERE, String.format("\nImproper command-line argument structure: %s\n" +
                        "\tShould be of the form: \"gradle runClient -Pport = <some port int> -Phost = <some host IP address>%n", Arrays.toString(args)));
                System.exit(1);
            }
        }

        address.set(parsedAddress);
        port.set(parsedPort);

        try {
            udpSocket = new DatagramSocket();
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, String.format("%nSomething went wrong while connecting to the UDP socket. Address: %s\tPort: %d%n", parsedAddress, parsedPort));
            e.printStackTrace();
            System.exit(-1);
        }
        gameGui = new ClientGui();
        gameGui.show(false);

        initializeGame();
        playGame();
    }

    private static void playGame() throws IOException, InterruptedException {
        while (!gameOver) {
            sendProtocolOut(new CustomProtocol(new CustomProtocolHeader(CustomProtocolHeader.Operation.QUESTION, "16", "json"),
                    null, UUID.randomUUID().toString()), address.get(), port.get());
            DatagramPacket question = receiveCustomProtocol();
            address.set(question.getAddress());
            port.set(question.getPort());
            CustomProtocol userResponse;

            do {
                userResponse = getUserResponse(readCustomProtocol(new DataInputStream(new ByteArrayInputStream(question.getData()))));
            } while (userResponse == null || userResponse.equals(question));

            sendProtocolOut(userResponse, address.get(), port.get());

            DatagramPacket serverResponse = receiveCustomProtocol();
            address.set(serverResponse.getAddress());
            port.set(serverResponse.getPort());

            CustomProtocol serverResponseProtocol = readCustomProtocol(new DataInputStream(new ByteArrayInputStream(serverResponse.getData())));
            gameOver = serverResponseProtocol.getPayload().isGameOver();
            if (serverResponseProtocol.getPayload().answeredCorrectly()) {
                numberOfCorrectResponses++;
            }
            insertPayloadImage(serverResponseProtocol.getPayload(), gameGui);
            gameGui.outputPanel.appendOutput(serverResponseProtocol.getPayload().getMessage());
        }
        endGame();
    }

    private static CustomProtocol getUserResponse(CustomProtocol question) {
        gameGui.outputPanel.appendOutput(question.getPayload().getMessage());
        try {
            Awaitility.await().atMost(60, TimeUnit.SECONDS).until(() -> gameGui.userInputCompleted());
            gameGui.setUserInputCompleted(false);
        } catch (Exception e) {
            return null;
        }
        CustomProtocolHeader.Operation responseOperation = (gameGui.isSolve() ? CustomProtocolHeader.Operation.SOLVE : CustomProtocolHeader.Operation.ANSWER);

        Payload userResponse = new Payload(null, gameGui.outputPanel.getCurrentInput(), false, false, false);
        CustomProtocolHeader userResponseHeader = new CustomProtocolHeader(responseOperation, "16", "json");
        return new CustomProtocol(userResponseHeader, userResponse, UUID.randomUUID().toString());
    }

    private static void initializeGame() throws IOException, InterruptedException {
        CustomProtocol initializeProtocol = new CustomProtocol(new CustomProtocolHeader(CustomProtocolHeader.Operation.INITIALIZE, "16",
                "json"), null, UUID.randomUUID().toString());
        sendProtocolOut(initializeProtocol, address.get(), port.get());

        DatagramPacket initializePacket = receiveCustomProtocol();

        initializeProtocol = readCustomProtocol(new DataInputStream(new ByteArrayInputStream(initializePacket.getData())));
        if (serverIsBusy(initializeProtocol)) {
            endGame();
            return;
        }
        gameGui.outputPanel.appendOutput(initializeProtocol.getPayload().getMessage());

        initializePacket = receiveCustomProtocol();
        address.set(initializePacket.getAddress());
        port.set(initializePacket.getPort());
        initializeProtocol = readCustomProtocol(new DataInputStream(new ByteArrayInputStream(initializePacket.getData())));
        if (serverIsBusy(initializeProtocol)) {
            endGame();
            return;
        }
        gameGui.outputPanel.appendOutput(initializeProtocol.getPayload().getMessage());

        while (gridDimension < 2) {
            Awaitility.await().atMost(30, TimeUnit.SECONDS).until(() -> gameGui.userInputCompleted());
            gameGui.setUserInputCompleted(false);
            gridDimension = parseInt(gameGui.outputPanel.getCurrentInput());
            if (gridDimension < 2) {
                gameGui.outputPanel.appendOutput(GAME_INITIALIZATION_ERROR_MESSAGE);
            }
        }
        gameGui.newGame(gridDimension);

        Payload initializePayload = new Payload(null, String.valueOf(gridDimension), false, false, false);
        sendProtocolOut(new CustomProtocol(new CustomProtocolHeader(CustomProtocolHeader.Operation.INITIALIZE, "16", "json"),
                initializePayload, UUID.randomUUID().toString()), address.get(), port.get());
    }

    private static boolean serverIsBusy(CustomProtocol initializeProtocol) {
        return initializeProtocol.getHeader().getOperation() == CustomProtocolHeader.Operation.BUSY;
    }


    private static void sendProtocolOut(CustomProtocol protocol, InetAddress address, int port) throws IOException {
        byteBuffer.clear();
        writeCustomProtocolOut(byteBuffer, protocol);
        DatagramPacket initializePacket = new DatagramPacket(byteBuffer.array(), byteBuffer.array().length, address, port);
        udpSocket.send(initializePacket);
        byteBuffer.clear();
    }

    private static DatagramPacket receiveCustomProtocol() throws IOException {
        DatagramPacket initializePacket = new DatagramPacket(byteBuffer.array(), byteBuffer.limit());
        udpSocket.receive(initializePacket);
        return initializePacket;
    }

    public static void endGame() throws InterruptedException {
        gameGui.outputPanel.appendOutput("Shutting down...");
        CustomProtocolHeader shutdownHeader = new CustomProtocolHeader(CustomProtocolHeader.Operation.SHUTDOWN, "16", "json");
        try {
            sendProtocolOut(new CustomProtocol(shutdownHeader, null, UUID.randomUUID().toString()), address.get(), port.get());
        } catch (IOException e) {
            /*IGNORE*/
        }
        Thread.sleep(3_000);
        gameGui.close();
        udpSocket.close();
        Runtime.getRuntime().exit(0);
    }

    private static void insertPayloadImage(Payload serverPayload, ClientGui gameGui) throws IOException {
        if (numberOfCorrectResponses <= (gridDimension * gridDimension) && serverPayload.getBase64encodedCroppedImage() != null) {
            BufferedImage image = convertBase64encodedStringToBufferedImage(serverPayload.getBase64encodedCroppedImage());
            int row = (numberOfCorrectResponses - 1) / gridDimension;
            int col = (numberOfCorrectResponses - 1) % gridDimension;
            col = (col >= 0 && col <= gridDimension ? col : gridDimension - 1);
            int finalCol = col;
            LOGGER.log(Level.INFO, () -> String.format("%nInserting a new image in row: %d\tcol: %d%n", row, finalCol));
            gameGui.insertImage(image, row, col);
        }
    }

}
