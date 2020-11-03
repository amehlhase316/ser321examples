package ser321wk3.server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import ser321wk3.Payload;

import static ser321wk3.CustomTCPUtilities.jvmIsShuttingDown;
import static ser321wk3.CustomTCPUtilities.parseInt;
import static ser321wk3.CustomTCPUtilities.setReceivedData;
import static ser321wk3.CustomTCPUtilities.waitForData;
import static ser321wk3.CustomTCPUtilities.writePayloadOut;

public class TiledRebusGameTCPServer {

    private static final List<Connection> connectedClients = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(TiledRebusGameTCPServer.class.getName());

    public static void main(String[] args) {

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

    private static void startServer(int parsedPort) {

        try (ServerSocket listener = new ServerSocket(parsedPort)) {

            RebusPuzzleGameController gameController = new RebusPuzzleGameController();
            while (!jvmIsShuttingDown()) {
                Socket socket = listener.accept();
                Connection newConnection = new Connection(socket, gameController);
                connectedClients.add(newConnection);
            }
        } catch (IOException e) {
            for (Connection client : connectedClients) {
                shutdownClient(client);
            }
            e.printStackTrace();
        }
    }

    private static final class Connection extends Thread {
        private final RebusPuzzleGameController gameController;
        private final Socket clientSocket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        private boolean isPrimaryConnection;

        public Connection(final Socket clientSocket, final RebusPuzzleGameController gameController) {
            this.clientSocket = clientSocket;
            this.gameController = gameController;
            try {
                inputStream = new DataInputStream(clientSocket.getInputStream());
                outputStream = new DataOutputStream(clientSocket.getOutputStream());
                this.start();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, () -> "Connection setup failed: " + e.getMessage());
                e.printStackTrace();
            }
        }

        public Socket getClientSocket() {
            return clientSocket;
        }

        @Override
        public void run() {
            final AtomicReference<Payload> payloadAtomicReference = new AtomicReference<>(null);
            if (gameController.getCurrentGame() == null) {
                try {
                    initializeGame(payloadAtomicReference);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Something went wrong initializing the game.");
                    e.printStackTrace();
                }
            }

            playGame(payloadAtomicReference);
        }

        private void playGame(AtomicReference<Payload> payloadAtomicReference) {
            do {
                Payload questionOut = new Payload(null, gameController.getCurrentQuestion().getQuestion(), false, false);
                LOGGER.info("Puzzle Answer: " + gameController.getCurrentGame().getRandomlySelectedRebus().getRebusAnswer());
                LOGGER.info("Question Answer: " + gameController.getCurrentQuestion().getAnswer());
                try {
                    writePayloadOut(questionOut, outputStream);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Something went wrong while sending a question from the server.");
                    e.printStackTrace();
                }

                waitForInputFromClient(payloadAtomicReference);
                LOGGER.info("Data received from client: " + payloadAtomicReference.get());

                Payload playResultOut = null;
                try {
                    playResultOut = play(payloadAtomicReference.get());
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Something went wrong while playing a round.");
                    e.printStackTrace();
                }
                try {
                    writePayloadOut(playResultOut, outputStream);
                    outputStream.flush();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Something went wrong emptying the output stream.");
                    e.printStackTrace();
                }
                setReceivedData(payloadAtomicReference, null);
                if (gameOver()) {
                    try {
                        askIfClientWantsToPlayAgain(payloadAtomicReference);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Something went wrong during the end of game sequence.");
                        e.printStackTrace();
                    }
                }
            } while (!gameOver());
        }

        private void askIfClientWantsToPlayAgain(AtomicReference<Payload> payloadAtomicReference) throws IOException {
            writePayloadOut(new Payload("Would you like to play another game?: y/N", gameController.wonGame(),
                    gameController.gameOver()), outputStream);
            waitForInputFromClient(payloadAtomicReference);
            if (!payloadAtomicReference.get().gameOver()) {
                resetGame(payloadAtomicReference);
            }
        }

        private void resetGame(AtomicReference<Payload> payloadAtomicReference) throws IOException {
            gameController.setGameOver(false);
            gameController.setWonGame(false);
            initializeGame(payloadAtomicReference);
        }

        private void initializeGame(AtomicReference<Payload> payloadAtomicReference) throws IOException {
            writePayloadOut(initializeRebusPuzzleGameRequest(), outputStream);
            outputStream.flush();
            waitForInputFromClient(payloadAtomicReference);

            LOGGER.log(Level.INFO, String.format("%nReceived payload from client: %s%n", payloadAtomicReference.get().toString()));
            int gridDimension = parseInt(payloadAtomicReference.get().getMessage());

            gameController.setCurrentGame(new PuzzleGame(gridDimension));
            gameController.setGridDimension(gridDimension);
            outputStream.flush();
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

        private void waitForInputFromClient(AtomicReference<Payload> payloadAtomicReference) {
            do {
                try {
                    waitForData(inputStream, null, payloadAtomicReference, 120);
                } catch (Exception e) {
                    /*IGNORE*/
                }
            } while (payloadAtomicReference.get() == null);
        }

        public Payload initializeRebusPuzzleGameRequest() throws IOException {
            gameController.setWonGame(false);
            gameController.setGameOver(false);
            return parsePayload(null, null, "Enter an int >= 2: ");
        }

        private Payload play(Payload playerResponse) throws IOException {
            final PuzzleQuestion currentQuestion = gameController.getCurrentQuestion();
            final String playerResponseMessage = playerResponse.getMessage();
            boolean solved = gameController.getCurrentGame().getRandomlySelectedRebus().isCorrect(playerResponseMessage);
            boolean answeredCorrectly = gameController.getCurrentGame().answerPuzzleQuestion(currentQuestion, playerResponseMessage);
            boolean playerLost = gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly() ==
                    RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            gameController.setCurrentQuestion();
            if (solved) {
                gameController.setWonGame(true);
                gameController.setGameOver(true);
                gameController.getCurrentGame().answerPuzzleQuestion(currentQuestion, currentQuestion.getAnswer());

                return parsePayload(baos, gameController.getCroppedImages().get(gameController.getCroppedImages().size() - 1), "Congratulations! You've Won!");
            } else if (answeredCorrectly) {
                int bufferedImageIndex = gameController.getCurrentGame().getNumberOfQuestionsAnsweredCorrectly() - 1;
                gameController.setWonGame(false);
                gameController.setGameOver(false);
                return parsePayload(baos, gameController.getCroppedImages().get(bufferedImageIndex), "You answered correctly!");

            } else if (playerLost) {
                gameController.setGameOver(true);
                return new Payload(null, "Terribly sorry, but you have lost the game.", false, true);
            } else {
                gameController.setWonGame(false);
                gameController.setGameOver(false);
                return parsePayload(baos, null,
                        String.format("Terribly sorry but you've answered incorrectly. You have %d incorrect responses remaining.",
                                RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS - gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly()));
            }
        }

        public Payload parsePayload(ByteArrayOutputStream baos, BufferedImage croppedImage, String message) throws IOException {

            if (croppedImage != null) {
                return new Payload(extractImageBytes(croppedImage, baos), message, gameController.wonGame(), gameController.gameOver());
            }
            return new Payload(null, message, gameController.wonGame(), gameController.gameOver());
        }

        private byte[] extractImageBytes(BufferedImage image, ByteArrayOutputStream baos) throws IOException {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        }

        private boolean gameOver() {
            return gameController.gameOver();
        }
    }
}
