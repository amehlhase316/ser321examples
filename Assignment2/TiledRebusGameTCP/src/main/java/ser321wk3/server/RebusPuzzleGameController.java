package ser321wk3.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RebusPuzzleGameController {

    public static final int NUMBER_OF_POSSIBLE_WRONG_ANSWERS = 3;
    private static final List<PuzzleQuestion> usedQuestions = new ArrayList<>();
    private List<File> croppedImages;
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

    public List<File> getCroppedImages() {
        return croppedImages;
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

    public void fillCroppedImages() throws IOException {
        croppedImages = GridMaker.main(new String[]{currentGame.getRandomlySelectedRebus().getRebusImageFile().getAbsolutePath(), String.valueOf(gridDimension)});
    }
}
