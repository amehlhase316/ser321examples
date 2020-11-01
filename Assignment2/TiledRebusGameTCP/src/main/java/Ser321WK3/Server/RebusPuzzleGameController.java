package Ser321WK3.Server;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class RebusPuzzleGameController {

    public static final int NUMBER_OF_POSSIBLE_WRONG_ANSWERS = 3;
    private final static List<PuzzleQuestion> usedQuestions = new ArrayList<>();
    private List<BufferedImage> croppedImages;
    private int gridDimension;
    private boolean wonGame;
    private boolean gameOver;
    private PuzzleGame currentGame;
    private PuzzleQuestion currentQuestion;

    public RebusPuzzleGameController() {
        this(null, 0);
    }

    public RebusPuzzleGameController(PuzzleGame currentGame, final int gridDimension) {
        this.currentGame = currentGame;
        this.gridDimension = gridDimension;
    }

    public static List<PuzzleQuestion> getUsedQuestions() {
        return usedQuestions;
    }

    public static int convertGridDimensionToNumberOfQuestionsAvailable(final int gridDimension) {
        return ((gridDimension * gridDimension) + NUMBER_OF_POSSIBLE_WRONG_ANSWERS);
    }

    public List<BufferedImage> getCroppedImages() {
        return croppedImages;
    }

    public void fillCroppedImages() {
        this.croppedImages = new ArrayList<>();
        BufferedImage image = currentGame.getRandomlySelectedRebus().getRebusImage();
        int divisibleHeight = image.getHeight() - (image.getHeight() % gridDimension);
        int divisibleWidth = image.getWidth() - (image.getWidth() % gridDimension);
        image = GridMaker.resize(image, divisibleWidth, divisibleHeight);

        int cellHeight = divisibleHeight / gridDimension;
        int cellWidth = divisibleWidth / gridDimension;

        for (int r = 0; r < gridDimension; r++) {
            for (int c = 0; c < gridDimension; c++) {
                BufferedImage croppedImage = GridMaker.cropImage(image, c * cellWidth, r * cellHeight, cellWidth, cellHeight);
                this.croppedImages.add(croppedImage);
            }
        }
    }

    public boolean gameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean wonGame() {
        return wonGame;
    }

    public void setWonGame(boolean wonGame) {
        this.wonGame = wonGame;
    }

    public PuzzleQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion() {
        do {
            this.currentQuestion = currentGame.getRandomlySelectedQuestion();
        } while (usedQuestions.contains(this.currentQuestion));
        usedQuestions.add(currentQuestion);
    }

    public int getGridDimension() {
        return gridDimension;
    }

    public void setGridDimension(int gridDimension) {
        this.gridDimension = gridDimension;
    }

    public PuzzleGame getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(PuzzleGame currentGame) {
        this.currentGame = currentGame;
    }
}
