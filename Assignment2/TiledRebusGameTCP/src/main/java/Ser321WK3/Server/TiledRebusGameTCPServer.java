package Ser321WK3.Server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
            AtomicReference<Payload> payloadAtomicReference = new AtomicReference<>(null);
            if (gameController.getCurrentGame() == null) {
                try {
                    initializeGame(payloadAtomicReference);
                } catch (IOException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    System.out.println("Something went wrong initializing the game.");
                    e.printStackTrace();
                }
            }

            do {
                Payload questionOut = new Payload(null, gameController.getCurrentQuestion().getQuestion(), false, false);
                try {
                    writePayloadOut(questionOut, outputStream);
                } catch (IOException e) {
                    System.out.println("Something went wrong while asking a question from the server.");
                    e.printStackTrace();
                }

                waitForInputFromClient(payloadAtomicReference);
                System.out.println("Data received from client: " + payloadAtomicReference.get());

                Payload playResultOut = play(payloadAtomicReference.get());
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

        private void initializeGame(AtomicReference<Payload> payloadAtomicReference) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            writePayloadOut(initializeRebusPuzzleGameRequest(), outputStream);
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

        public Payload initializeRebusPuzzleGameRequest() {
            gameController.setWonGame(false);
            gameController.setGameOver(false);
            return parsePayload(null, null, "Enter an int >= 2: ");
        }

        private Payload play(Payload playerResponse) {
            final PuzzleQuestion currentQuestion = gameController.getCurrentQuestion();
            final String playerResponseMessage = playerResponse.getMessage();
            boolean answeredCorrectly = gameController.getCurrentGame().answerPuzzleQuestion(currentQuestion, playerResponseMessage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            gameController.setCurrentQuestion();
            if (gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly() ==
                    RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS) {
                return new Payload(null, "Terribly sorry, but you have lost the game.", false, true);
            } else if (gameController.getCurrentGame().getRandomlySelectedRebus().isCorrect(playerResponseMessage)) {
                gameController.setWonGame(true);
                gameController.setGameOver(true);

                return parsePayload(baos, gameController.getCroppedImages(), "Congratulations! You've Won!");
            } else if (answeredCorrectly) {
                int bufferedImageIndex = gameController.getCurrentGame().getNumberOfQuestionsAnsweredCorrectly();
                gameController.setWonGame(false);
                gameController.setGameOver(false);
                return parsePayload(baos, gameController.getCroppedImages().subList(0, bufferedImageIndex), "You answered correctly!");

            } else {
                gameController.setWonGame(false);
                gameController.setGameOver(false);
                return parsePayload(baos, null,
                        String.format("Terribly sorry but you've answered incorrectly. You have %d incorrect responses remaining.",
                                RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS - gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly()));
            }
        }

        public Payload parsePayload(ByteArrayOutputStream baos, List<BufferedImage> croppedImages, String message) {

            if (croppedImages != null) {
                return new Payload(croppedImages.stream().map(bufferedImage -> {
                    try {
                        ImageIO.write(bufferedImage, "jpg", baos);
                        baos.flush();
                    } catch (IOException e) {
                        /*IGNORED*/
                    }
                    return baos.toByteArray();
                }).collect(Collectors.toList()),
                        message,
                        gameController.wonGame(),
                        gameController.gameOver());
            }
            return new Payload(null, message, gameController.wonGame(), gameController.gameOver());
        }

        private boolean gameIsNotOver() {
            return gameController.gameOver();
        }
    }
}
