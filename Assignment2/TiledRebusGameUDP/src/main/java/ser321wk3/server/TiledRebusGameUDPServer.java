package ser321wk3.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import ser321wk3.CustomProtocol;
import ser321wk3.CustomProtocolHeader;
import ser321wk3.Payload;

import static ser321wk3.CustomTCPUtilities.convertImageFileToBase64encodedString;
import static ser321wk3.CustomTCPUtilities.jvmIsShuttingDown;
import static ser321wk3.CustomTCPUtilities.parseInt;
import static ser321wk3.CustomTCPUtilities.setReceivedData;
import static ser321wk3.CustomTCPUtilities.waitForData;
import static ser321wk3.CustomTCPUtilities.writeCustomProtocolOut;

public class TiledRebusGameUDPServer {

    private static final List<Connection> connectedClients = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(TiledRebusGameUDPServer.class.getName());
    private static final AtomicBoolean BUSY = new AtomicBoolean(false);

    public static void main(String[] args) throws IOException {

        int parsedPort = 0;
        try {
            parsedPort = parseInt(args[0]);
        } catch (Exception e) {
            e.printStackTrace();

            LOGGER.log(Level.SEVERE, "\nImproper command-line argument structure: %s\n" +
                    "\tShould be of the form: \"gradle runServer -Pport = <some port int>%n", Arrays.toString(args));
            System.exit(1);
        }
        startServer(parsedPort);
    }

    private static void shutdownClient(Connection client) {
        try {
            client.getClientSocket().close();
        } catch (IOException ioException) {
            /*IGNORE*/
        }
    }

    private static void startServer(int parsedPort) throws IOException {

        try (ServerSocket listener = new ServerSocket(parsedPort)) {
            while (!jvmIsShuttingDown()) {
                Socket clientListener = null;
                try {
                    clientListener = listener.accept();
                    DataInputStream inputStream = new DataInputStream(clientListener.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(clientListener.getOutputStream());
                    if (!BUSY.get()) {
                        Connection clientConnection = new Connection(new RebusPuzzleGameController(), clientListener, inputStream, outputStream);
                        connectedClients.add(clientConnection);
                        clientConnection.start();
                        BUSY.set(true);
                    } else {
                        Payload rejectConnection = new Payload(null,
                                "Sorry, but the server is currently busy. Try again later.", false, false, false);
                        CustomProtocolHeader header = new CustomProtocolHeader(CustomProtocolHeader.Operation.BUSY, "16", "json");
                        writeCustomProtocolOut(outputStream, new CustomProtocol(header, rejectConnection));
                    }
                } catch (Exception e) {
                    clientListener.close();
                    LOGGER.log(Level.SEVERE, () -> "Something went wrong while starting a new client.");
                }
            }
        } catch (IOException e) {
            for (Connection clientConnection : connectedClients) {
                shutdownClient(clientConnection);
            }
            e.printStackTrace();
        }
    }

    private static final class Connection extends Thread {
        private final RebusPuzzleGameController gameController;
        private final Socket clientSocket;
        private final DataInputStream inputStream;
        private final DataOutputStream outputStream;
        private boolean isPrimaryConnection;

        public Connection(RebusPuzzleGameController gameController,
                          Socket clientSocket,
                          DataInputStream inputStream,
                          DataOutputStream outputStream) {
            this.gameController = gameController;
            this.clientSocket = clientSocket;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
            Runtime.getRuntime().addShutdownHook(this);
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        @Override
        public void run() {
            final AtomicReference<CustomProtocol> payloadAtomicReference = new AtomicReference<>(null);
            if (gameController.getCurrentGame() == null) {
                try {
                    initializeGame(payloadAtomicReference);
                } catch (IOException | InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Something went wrong initializing the game.");
                    e.printStackTrace();
                }
            }

            try {
                playGame(payloadAtomicReference);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            try {
                clientSocket.close();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                /*IGNORE*/
            }
        }

        private void playGame(AtomicReference<CustomProtocol> protocolAtomicReference) throws IOException, InterruptedException {
            do {
                Payload questionOut = new Payload(null, gameController.getCurrentQuestion().getQuestion(), false, false, false);
                LOGGER.info("Puzzle Answer: " + gameController.getCurrentGame().getRandomlySelectedRebus().getRebusAnswer());
                LOGGER.info("Question Answer: " + gameController.getCurrentQuestion().getAnswer());
                CustomProtocolHeader header = new CustomProtocolHeader(CustomProtocolHeader.Operation.QUESTION, "16", "json");
                try {
                    writeCustomProtocolOut(outputStream, new CustomProtocol(header, questionOut));
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Something went wrong while sending a question from the server.");
                    e.printStackTrace();
                    closeConnection();
                }

                setReceivedData(protocolAtomicReference, null);
                waitForInputFromClient(protocolAtomicReference);
                LOGGER.info("Question response received from client: " + protocolAtomicReference.get());

                Payload playResultOut = null;
                try {
                    playResultOut = play(protocolAtomicReference);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Something went wrong while playing a round.");
                    e.printStackTrace();
                    closeConnection();
                }
                CustomProtocolHeader responseHeader;
                try {
                    if (playResultOut == null) {
                        closeConnection();
                    }
                    if (playResultOut.isGameOver()) {
                        responseHeader = new CustomProtocolHeader(CustomProtocolHeader.Operation.SHUTDOWN, "16", "json");
                        writeCustomProtocolOut(outputStream, new CustomProtocol(responseHeader, playResultOut));
                        closeConnection();
                    } else {
                        responseHeader = new CustomProtocolHeader(CustomProtocolHeader.Operation.RESPONSE, "16", "json");
                    }
                    writeCustomProtocolOut(outputStream, new CustomProtocol(responseHeader, playResultOut));
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Something went wrong emptying the output stream.");
                    e.printStackTrace();
                    closeConnection();
                }
            } while (!gameOver());
            closeConnection();
        }

        private void closeConnection() throws IOException, InterruptedException {
            deleteTempFilesOnExit();
            BUSY.set(false);
            LOGGER.info("Game has concluded. Shutting down the client...");
            Thread.sleep(1_000);
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        }

        private void deleteTempFilesOnExit() {
            for (File tempFile : gameController.getCroppedImages()) {
                tempFile.delete();
            }
        }

        private void initializeGame(AtomicReference<CustomProtocol> payloadAtomicReference) throws IOException, InterruptedException {
            CustomProtocolHeader initializeHeader = new CustomProtocolHeader(CustomProtocolHeader.Operation.INITIALIZE, "16", "json");
            writeCustomProtocolOut(outputStream, new CustomProtocol(initializeHeader, initializeRebusPuzzleGameRequest()));
            waitForInputFromClient(payloadAtomicReference);

            LOGGER.log(Level.INFO, String.format("%nReceived payload from client: %s%n", payloadAtomicReference.get().toString()));
            int gridDimension = 0;
            try {
                gridDimension = parseInt(payloadAtomicReference.get().getPayload().getMessage());
            } catch (Exception e) {
                closeConnection();
            }

            gameController.setCurrentGame(new PuzzleGame(gridDimension));
            gameController.setGridDimension(gridDimension);
            gameController.setCurrentQuestion();
            gameController.fillCroppedImages();
            setPrimaryConnection(true);
            setReceivedData(payloadAtomicReference, null);
        }

        public boolean primaryConnection() {
            return isPrimaryConnection;
        }

        public void setPrimaryConnection(boolean primaryConnection) {
            isPrimaryConnection = primaryConnection;
        }

        private void waitForInputFromClient(AtomicReference<CustomProtocol> protocolAtomicReference) throws IOException, InterruptedException {
            do {
                try {
                    waitForData(inputStream, null, protocolAtomicReference, 120);
                } catch (Exception e) {
                    /*IGNORE*/
                }
            } while (protocolAtomicReference.get() == null);
            if (protocolAtomicReference.get().getHeader().getOperation() == CustomProtocolHeader.Operation.SHUTDOWN) {
                closeConnection();
            }
        }

        public Payload initializeRebusPuzzleGameRequest() {
            gameController.setWonGame(false);
            gameController.setGameOver(false);
            return new Payload(null, "Enter an int >= 2: ", false, false, false);
        }

        private Payload play(AtomicReference<CustomProtocol> playerResponse) throws IOException, InterruptedException {
            if (playerResponse.get().getHeader().getOperation() == CustomProtocolHeader.Operation.SHUTDOWN) {
                closeConnection();
            }
            final PuzzleQuestion currentQuestion = gameController.getCurrentQuestion();
            final String playerResponseMessage = playerResponse.get().getPayload().getMessage();
            boolean solved = gameController.getCurrentGame().getRandomlySelectedRebus().isCorrect(playerResponseMessage);
            boolean answeredCorrectly = gameController.getCurrentGame().answerPuzzleQuestion(currentQuestion, playerResponseMessage);
            boolean playerLost = gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly() ==
                    RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS;
            gameController.setCurrentQuestion();
            String base64EncodedImage;
            if (playerResponse.get().getHeader().getOperation() == CustomProtocolHeader.Operation.SOLVE) {
                gameController.setWonGame(solved);
                gameController.setGameOver(true);
                gameController.getCurrentGame().answerPuzzleQuestion(currentQuestion, currentQuestion.getAnswer());

                if (solved) {
                    base64EncodedImage = convertImageFileToBase64encodedString(gameController.getCroppedImages().get(gameController.getCroppedImages().size() - 1));
                    return new Payload(base64EncodedImage, "Congratulations! You've Won!", true, true, true);
                } else {
                    return new Payload(null, "Unfortunately you guessed incorrectly. The game is over.", gameController.wonGame(),
                            gameController.gameOver(), false);
                }
            } else if (playerResponse.get().getHeader().getOperation() == CustomProtocolHeader.Operation.ANSWER) {
                int bufferedImageIndex = gameController.getCurrentGame().getNumberOfQuestionsAnsweredCorrectly() - 1;
                gameController.setWonGame(false);
                gameController.setGameOver(gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly() == RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS);

                if (answeredCorrectly) {
                    base64EncodedImage = convertImageFileToBase64encodedString(gameController.getCroppedImages().get(bufferedImageIndex));
                    return new Payload(base64EncodedImage, "You answered correctly!", false, false, true);
                } else {
                    if (gameOver()) {
                        return new Payload(null, String.format("Terribly sorry but you've answered incorrectly %d times"
                                + " and thus the game is over", gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly()),
                                false, true, false);
                    }
                    return new Payload(null,
                            String.format("Terribly sorry but you've answered incorrectly. You have %d incorrect responses remaining.",
                                    RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS - gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly()),
                            false, false, false);
                }
            } else if (playerLost) {
                gameController.setGameOver(true);
                return new Payload(null, "Terribly sorry, but you have lost the game.", false, true, false);
            }
            return null;
        }

        private boolean gameOver() {
            return gameController.gameOver();
        }
    }
}
