package Ser321WK3.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import Ser321WK3.Payload;

import static Ser321WK3.CustomTCPUtilities.parseInt;
import static Ser321WK3.CustomTCPUtilities.parsePayload;
import static Ser321WK3.CustomTCPUtilities.setReceivedData;
import static Ser321WK3.CustomTCPUtilities.waitForData;

public class TiledRebusGameTCPServer {

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

        final RebusPuzzleGameController gameController;
        try {
            ServerSocket listener = new ServerSocket(parsedPort);
            gameController = new RebusPuzzleGameController();
            while (true) {
                Socket socket = listener.accept();
                new Connection(socket, gameController);
            }
        } catch (IOException e) {
            System.out.println("Error while listening on socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final class Connection extends Thread {
        private final RebusPuzzleGameController gameController;
        private final Socket clientSocket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

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

        @Override
        public void run() {
            Payload parsedPayload;
            AtomicReference<String> receivedDataString = new AtomicReference<>("");
            try {
                if (gameController.getCurrentGame() == null) {
                    outputStream.writeUTF(initializeRebusPuzzleGameRequest().toString());
                    waitForInputFromClient(receivedDataString);

                    parsedPayload = parsePayload(receivedDataString.get());
                    setReceivedData(receivedDataString, "");

                    System.out.printf("%nReceived payload from Client: %s%n", parsedPayload.toString());
                    int gridDimension = parseInt(parsedPayload.getMessage());
                    gameController.setCurrentGame(new PuzzleGame(gridDimension));
                    gameController.setGridDimension(gridDimension);
                    outputStream.flush();
                    gameController.setCurrentQuestion();
                    gameController.fillCroppedImages();
                }
                do {
                    Payload questionOut = new Payload(gameController.getCurrentQuestion().getQuestion(), false, false);
                    outputStream.writeUTF(questionOut.toString());

                    waitForInputFromClient(receivedDataString);
                    System.out.println("Data received from client: " + receivedDataString.get());
                    parsedPayload = parsePayload(receivedDataString.get());
                    setReceivedData(receivedDataString, "");

                    Payload playResultOut = play(parsedPayload);
                    outputStream.writeUTF(playResultOut.toString());
                    outputStream.flush();
                } while (gameIsNotOver());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    /*IGNORED*/
                }
            }
        }

        private void waitForInputFromClient(AtomicReference<String> receivedDataString) throws IOException {
            do {
                try {
                    waitForData(inputStream, null, receivedDataString, 120);
                } catch (Exception e) {
                    /*IGNORE*/
                }
            } while (receivedDataString.get().isEmpty());
        }

        public Payload initializeRebusPuzzleGameRequest() {
            String message = "Enter an int >= 2: ";
            return new Payload(message, false, false);
        }

        private Payload play(Payload playerResponse) {
            final PuzzleQuestion currentQuestion = gameController.getCurrentQuestion();
            final String playerResponseMessage = playerResponse.getMessage();
            boolean answeredCorrectly = gameController.getCurrentGame().answerPuzzleQuestion(currentQuestion, playerResponseMessage);
            gameController.setCurrentQuestion();
            if (gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly() ==
                    RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS) {
                return new Payload("Terribly sorry, but you have lost the game.", false, true);
            } else if (gameController.getCurrentGame().getRandomlySelectedRebus().isCorrect(playerResponseMessage)) {
                gameController.setWonGame(true);
                gameController.setGameOver(true);
                return new Payload(gameController.getCroppedImages(),
                        "Congratulations! You've Won!",
                        gameController.wonGame(),
                        gameController.gameOver());
            } else if (answeredCorrectly) {
                int bufferedImageIndex = gameController.getCurrentGame().getNumberOfQuestionsAnsweredCorrectly() - 1;
                return new Payload(gameController.getCroppedImages().subList(0, bufferedImageIndex),
                        "You answered correctly!",
                        false,
                        false);
            } else {
                return new Payload(String.format("Terribly sorry but you've answered incorrectly. You have %d incorrect responses remaining.",
                        RebusPuzzleGameController.NUMBER_OF_POSSIBLE_WRONG_ANSWERS - gameController.getCurrentGame().getNumberOfQuestionsAnsweredIncorrectly()),
                        false,
                        false);
            }
        }

        private boolean gameIsNotOver() {
            return gameController.gameOver();
        }
    }
}
