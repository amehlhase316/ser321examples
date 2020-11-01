package Ser321WK3.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import Ser321WK3.Payload;

import static Ser321WK3.CustomTCPUtilities.parseInt;
import static Ser321WK3.CustomTCPUtilities.parsePayload;

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
        try (ServerSocket listener = new ServerSocket(parsedPort)) {
            gameController = new RebusPuzzleGameController();
            while (true) {
                try (Socket socket = listener.accept()) {
                    new Connection(socket, gameController);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while listening on socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final class Connection extends Thread {
        private static final int DELAY = 1_000;
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
            String receivedData;
            boolean gameIsNull = (gameController.getCurrentGame() == null);
            try {
                do {
                    if (gameIsNull) {
                        outputStream.writeUTF(initializeRebusPuzzleGameRequest().toString());
                        parsedPayload = parsePayload(inputStream.readUTF());
                        gameIsNull = currentGameIsNull(parsedPayload);
                    }

                    if (!gameIsNull) {
                        gameController.setCurrentQuestion();
                        gameController.fillCroppedImages();
                        Payload questionOut = new Payload(gameController.getCurrentQuestion().getQuestion(), false, false);
                        outputStream.writeUTF(questionOut.toString());
                        receivedData = inputStream.readUTF();
                        System.out.println("Data Received: " + receivedData);
                        parsedPayload = parsePayload(receivedData);
                        Payload playResultOut = play(parsedPayload);
                        outputStream.writeUTF(playResultOut.toString());
                    }
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

        private boolean gameIsNotOver() {
            return gameController.gameOver();
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

        private boolean currentGameIsNull(Payload parsedPayload) throws IOException {
            try {
                final int gridDimension = parseInt(parsedPayload.getMessage().strip());
                gameController.setGridDimension(gridDimension);
                final int numberOfPuzzleQuestionsAvailable =
                        RebusPuzzleGameController.convertGridDimensionToNumberOfQuestionsAvailable(gridDimension);
                gameController.setCurrentGame(new PuzzleGame(numberOfPuzzleQuestionsAvailable));
                return false;
            } catch (IOException | NumberFormatException e) {
                String message = String.format("Improperly formatted response: %s.%nPlease enter an int >= 2", parsedPayload.getMessage());
                outputStream.writeUTF((new Payload(message, false, false)).toString());
                return true;
            }
        }

        public Payload initializeRebusPuzzleGameRequest() {
            String message = "Enter an int >= 2: ";
            return new Payload(message, false, false);
        }

        public DataInputStream getInputStream() {
            return inputStream;
        }

        public DataOutputStream getOutputStream() {
            return outputStream;
        }

        public Socket getClientSocket() {
            return clientSocket;
        }
    }
}
