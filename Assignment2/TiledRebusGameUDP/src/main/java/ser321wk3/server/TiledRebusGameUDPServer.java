package ser321wk3.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import ser321wk3.CustomProtocol;
import ser321wk3.CustomProtocolHeader;
import ser321wk3.Payload;

import static ser321wk3.CustomUDPUtilities.BYTE_ARRAY_SIZE;
import static ser321wk3.CustomUDPUtilities.convertImageFileToBase64encodedString;
import static ser321wk3.CustomUDPUtilities.parseInt;
import static ser321wk3.CustomUDPUtilities.readCustomProtocol;
import static ser321wk3.CustomUDPUtilities.writeCustomProtocolOut;
import static ser321wk3.server.RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS;

public class TiledRebusGameUDPServer {

    private static final Logger LOGGER = Logger.getLogger(TiledRebusGameUDPServer.class.getName());
    private static final AtomicBoolean BUSY = new AtomicBoolean(false);
    private static final ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_ARRAY_SIZE);
    private static boolean STOPPED = false;
    private static DatagramSocket udpSocket;
    private static RebusPuzzleGameController gameController;
    private static int gridDimension;
    private static boolean busy;

    public static void main(String[] args) throws IOException, InterruptedException {

        int parsedPort = 0;
        try {
            parsedPort = parseInt(args[0]);
        } catch (Exception e) {
            e.printStackTrace();

            LOGGER.log(Level.SEVERE, "\nImproper command-line argument structure: %s\n" +
                    "\tShould be of the form: \"gradle runServer -Pport = <some port int>%n", Arrays.toString(args));
            System.exit(1);
        }
        gameController = new RebusPuzzleGameController();
        startServer(parsedPort);
    }

    private static void startServer(int parsedPort) throws IOException, InterruptedException {

        udpSocket = new DatagramSocket(parsedPort);
        DatagramPacket packet = null;

        try {
            while (!STOPPED) {
                packet = new DatagramPacket(byteBuffer.array(), byteBuffer.limit());
                udpSocket.receive(packet);


                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.getData()));
                CustomProtocol receivedProtocol = readCustomProtocol(inputStream);

                switch (receivedProtocol.getHeader().getOperation()) {
                    case ANSWER:
                    case SOLVE:
                        play(receivedProtocol, packet);
                        break;
                    case BUSY:
                        break;
                    case INITIALIZE:
                        gameSetup(receivedProtocol, address, port);
                        break;
                    case QUESTION:
                        Payload questionOut = new Payload(null, gameController.getCurrentQuestion().getQuestion(),
                                false, false, false);
                        sendServerResponse(CustomProtocolHeader.Operation.QUESTION, questionOut, address, port);
                        LOGGER.info(() -> String.format("Question Answer: %s\tPuzzle Answer: %s", gameController.getCurrentQuestion().getAnswer(), gameController.getCurrentGame().getRandomlySelectedRebus().getRebusAnswer()));
                        break;
                    default:
                        STOPPED = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeSocket(packet);
        }

        closeSocket(packet);
    }

    private static void play(CustomProtocol playerResponse, DatagramPacket packet) throws IOException {
        final PuzzleQuestion currentQuestion = gameController.getCurrentQuestion();
        final String playerResponseMessage = playerResponse.getPayload().getMessage();
        boolean solved = gameController.getCurrentGame().getRandomlySelectedRebus().isCorrect(playerResponseMessage);
        boolean answeredCorrectly = gameController.getCurrentGame().answerPuzzleQuestion(currentQuestion, playerResponseMessage);
        boolean playerLost = gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly() ==
                NUMBER_OF_POSSIBLE_WRONG_ANSWERS;
        gameController.setCurrentQuestion();
        String base64EncodedImage;
        Payload serverResponseToPlayerPayload = null;
        if (playerLost) {
            gameController.setGameOver(true);
            serverResponseToPlayerPayload = new Payload(null, "Terribly sorry, but you have lost the game.",
                    false, true, false);
        } else if (playerResponse.getHeader().getOperation() == CustomProtocolHeader.Operation.SOLVE) {
            gameController.setWonGame(solved);
            gameController.setGameOver(true);
            gameController.getCurrentGame().answerPuzzleQuestion(currentQuestion, currentQuestion.getAnswer());

            if (solved) {
                base64EncodedImage = convertImageFileToBase64encodedString(gameController.getCroppedImages().get(gameController.getCroppedImages().size() - 1));
                serverResponseToPlayerPayload = new Payload(base64EncodedImage, "Congratulations! You've Won!", true, true, true);
            } else {
                serverResponseToPlayerPayload = new Payload(null, "Unfortunately you guessed incorrectly. The game is over.", gameController.wonGame(),
                        gameController.gameOver(), false);
            }
        } else if (playerResponse.getHeader().getOperation() == CustomProtocolHeader.Operation.ANSWER) {
            int bufferedImageIndex = gameController.getCurrentGame().getNumberOfQuestionsAnsweredCorrectly() - 1;
            gameController.setWonGame(false);
            gameController.setGameOver(false);
            String message = (answeredCorrectly ? "You answered correctly!" :
                    String.format("Terribly sorry but you've answered incorrectly. You have %d attempts remaining."
                            , NUMBER_OF_POSSIBLE_WRONG_ANSWERS - gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly()));
            base64EncodedImage = (answeredCorrectly ? convertImageFileToBase64encodedString(gameController.getCroppedImages().get(bufferedImageIndex)) : null);
            serverResponseToPlayerPayload = new Payload(base64EncodedImage, message, gameController.wonGame(), gameController.gameOver(), answeredCorrectly);
        }

        sendServerResponse(CustomProtocolHeader.Operation.RESPONSE, serverResponseToPlayerPayload, packet.getAddress(), packet.getPort());
    }

    private static void gameSetup(CustomProtocol protocol, InetAddress address, int port) throws IOException {

        Payload okPayload = new Payload(null, "Connection succeeded.", false, false, false);
        CustomProtocolHeader okHeader = new CustomProtocolHeader(CustomProtocolHeader.Operation.INITIALIZE, "16", "json");

        writeCustomProtocolOut(byteBuffer, new CustomProtocol(okHeader, okPayload, UUID.randomUUID().toString()));
        udpSocket.send(new DatagramPacket(byteBuffer.array(), byteBuffer.array().length, address, port));

        busy = true;

        if (gridDimension < 2) {
            CustomProtocolHeader initializeHeader = new CustomProtocolHeader(CustomProtocolHeader.Operation.INITIALIZE, "16", "json");
            CustomProtocol initializeProtocol = new CustomProtocol(initializeHeader, initializeRebusPuzzleGameRequest(), UUID.randomUUID().toString());

            writeCustomProtocolOut(byteBuffer, initializeProtocol);
            udpSocket.send(new DatagramPacket(byteBuffer.array(), byteBuffer.array().length, address, port));
            DatagramPacket newPacket = receiveNewPacket();
            InetAddress newAddress = newPacket.getAddress();
            int newPort = newPacket.getPort();
            CustomProtocol receivedProtocol = readCustomProtocol(new DataInputStream(new ByteArrayInputStream(newPacket.getData())));
            gridDimension = parseInt(receivedProtocol.getPayload().getMessage());
            if (gridDimension >= 2) {
                initializeCurrentGame(newAddress, newPort);
            }
        } else if (gameController.getCurrentGame() == null) {
            initializeCurrentGame(address, port);
        }

    }

    private static void sendServerResponse(CustomProtocolHeader.Operation operation, Payload payload, InetAddress address, int port) throws IOException {
        CustomProtocolHeader header = new CustomProtocolHeader(operation, "16", "json");
        CustomProtocol protocolOut = new CustomProtocol(header, payload, UUID.randomUUID().toString());

        writeCustomProtocolOut(byteBuffer, protocolOut);
        udpSocket.send(new DatagramPacket(byteBuffer.array(), byteBuffer.array().length, address, port));
    }

    private static void closeSocket(DatagramPacket packet) throws IOException, InterruptedException {

        busy = false;
        if (packet != null) {
            CustomProtocolHeader shutdownHeader = new CustomProtocolHeader(CustomProtocolHeader.Operation.SHUTDOWN, "16", "json");
            Payload shutdownPayload = new Payload(null, "Server is shutting down...", false, true, false);
            CustomProtocol shutdownProtocol = new CustomProtocol(shutdownHeader, shutdownPayload, UUID.randomUUID().toString());

            writeCustomProtocolOut(byteBuffer, shutdownProtocol);
            udpSocket.send(new DatagramPacket(byteBuffer.array(), byteBuffer.array().length, packet.getAddress(), packet.getPort()));
        }

        deleteTempFilesOnExit();
        BUSY.set(false);
        LOGGER.info("Game has concluded. Shutting down the client...");
        Thread.sleep(1_000);
        udpSocket.close();
    }

    public static Payload initializeRebusPuzzleGameRequest() {
        gameController.setWonGame(false);
        gameController.setGameOver(false);
        return new Payload(null, "Enter an int >= 2: ", false, false, false);
    }

    private static DatagramPacket receiveNewPacket() throws IOException {
        DatagramPacket initializationPacket = new DatagramPacket(byteBuffer.array(), byteBuffer.limit());
        udpSocket.receive(initializationPacket);
        return initializationPacket;
    }

    private static void initializeCurrentGame(InetAddress address, int port) throws IOException {
        gameController.setGridDimension(gridDimension);
        gameController.setCurrentGame(new PuzzleGame(gridDimension));
        gameController.setCurrentQuestion();
        gameController.fillCroppedImages();
    }

    private static void deleteTempFilesOnExit() {
        for (File tempFile : gameController.getCroppedImages()) {
            tempFile.delete();
        }
    }

    private static boolean gameOver() {
        return gameController.gameOver();
    }
}
