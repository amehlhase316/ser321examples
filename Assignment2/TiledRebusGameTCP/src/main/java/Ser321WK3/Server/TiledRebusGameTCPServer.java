package Ser321WK3.Server;

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

import javax.imageio.ImageIO;

import Ser321WK3.Payload;

import static Ser321WK3.CustomTCPUtilities.parseInt;
import static Ser321WK3.CustomTCPUtilities.setReceivedData;
import static Ser321WK3.CustomTCPUtilities.waitForData;
import static Ser321WK3.CustomTCPUtilities.writePayloadOut;

public class TiledRebusGameTCPServer {

    private static final List<Connection> connectedClients = new ArrayList<>();

    public static void main(String[] args) {

        int parsedPort = 0;
        try {
            parsedPort = parseInt(args[0]);
        } catch (Exception e) {
            e.printStackTrace();

            System.out.printf("\nImproper command-line argument structure: %s\n" +
                    "\tShould be of the form: \"gradle runServer -Pport = <some port int>%n", Arrays.toString(args));
            System.exit(1);
        }

        do {
            try {
                startServer(parsedPort);
            } catch (IOException e) {
                handleIOException(e);
            }
        } while (true);
    }

    private static void handleIOException(IOException e) {
        if (connectedClients.isEmpty()) {
            e.printStackTrace();
        } else {
            for (int i = 0; i < connectedClients.size(); i++) {
                shutdownClient(i);
            }
        }
    }

    private static void shutdownClient(int clientIndex) {
        try {
            Connection client = connectedClients.remove(clientIndex);
            client.getClientSocket().close();
        } catch (IOException ioException) {
            /*IGNORE*/
        }
    }

    private static void startServer(int parsedPort) throws IOException {
        ServerSocket listener = new ServerSocket(parsedPort);
        RebusPuzzleGameController gameController = new RebusPuzzleGameController();
        while (true) {
            Socket socket = listener.accept();
            connectedClients.add(new Connection(socket, gameController));
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
                System.out.println("Connection setup failed: " + e.getMessage());
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
                    System.out.println("Something went wrong initializing the game.");
                    e.printStackTrace();
                }
            }

            do {
                Payload questionOut = new Payload(null, gameController.getCurrentQuestion().getQuestion(), false, false);
                try {
                    writePayloadOut(questionOut, outputStream);
                } catch (IOException e) {
                    System.out.println("Something went wrong while sending a question from the server.");
                    e.printStackTrace();
                }

                waitForInputFromClient(payloadAtomicReference);
                System.out.println("Data received from client: " + payloadAtomicReference.get());

                Payload playResultOut = null;
                try {
                    playResultOut = play(payloadAtomicReference.get());
                } catch (IOException e) {
                    System.out.println("Something went wrong while playing a round.");
                    e.printStackTrace();
                }
                try {
                    writePayloadOut(playResultOut, outputStream);
                    outputStream.flush();
                } catch (IOException e) {
                    System.out.println("Something went wrong emptying the output stream.");
                    e.printStackTrace();
                }
                setReceivedData(payloadAtomicReference, null);
            } while (gameIsNotOver());
        }

        private void initializeGame(AtomicReference<Payload> payloadAtomicReference) throws IOException {
            writePayloadOut(initializeRebusPuzzleGameRequest(), outputStream);
            outputStream.flush();
            waitForInputFromClient(payloadAtomicReference);

            System.out.printf("%nReceived payload from Client: %s%n", payloadAtomicReference.get().toString());
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

        private boolean gameIsNotOver() {
            return !gameController.gameOver();
        }
    }
}
