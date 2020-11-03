package Ser321WK3.Server;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class PuzzleGame {

    public static final String REBUS_PUZZLES_JSON = "puzzles/RebusPuzzles.json";
    private static final String PUZZLE_QUESTIONS_FILE_PATH = "puzzles/PuzzleQuestions.json";
    private final List<PuzzleQuestion> gameQuestions;
    private final RawPuzzles rawPuzzles;
    private final int numberOfQuestionsAvailableToAnswer;
    private final Rebus randomlySelectedRebus;
    private int numberOfQuestionsAnsweredIncorrectly;
    private int numberOfQuestionsAnsweredCorrectly;

    public PuzzleGame(int numberOfQuestionsAvailableToAnswer) throws IOException {
        this(parsePuzzleQuestions(), Objects.requireNonNull(parseRebusPuzzleFilesWithAnswers()), numberOfQuestionsAvailableToAnswer);
    }

    public PuzzleGame(List<PuzzleQuestion> gameQuestions, RawPuzzles rawPuzzles, int numberOfQuestionsAvailableToAnswer) throws IOException {
        this.gameQuestions = gameQuestions;
        this.rawPuzzles = rawPuzzles;
        this.numberOfQuestionsAvailableToAnswer = numberOfQuestionsAvailableToAnswer;
        File randomlySelected = new File(rawPuzzles.getPuzzles().get(pickRandomly(0, rawPuzzles.getPuzzles().size())).getFileName());
        this.randomlySelectedRebus = new Rebus(convertFileToImage(randomlySelected), randomlySelected.getName());
    }

    private static List<PuzzleQuestion> parsePuzzleQuestions() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(Paths.get(PUZZLE_QUESTIONS_FILE_PATH).toFile(), PuzzleQuestions.class).getPuzzleQuestions();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(-1);
        return Collections.emptyList();
    }

    private static RawPuzzles parseRebusPuzzleFilesWithAnswers() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(Paths.get(REBUS_PUZZLES_JSON).toFile(), RawPuzzles.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int pickRandomly(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    private static List<File> getRebusImageFiles() {
        return Arrays.stream(new File("puzzles").listFiles())
                .filter(file -> file.getName().contains(".jpg") || file.getName().contains(".png"))
                .collect(Collectors.toList());
    }

    public RawPuzzles getRawPuzzles() {
        return rawPuzzles;
    }

    private BufferedImage convertFileToImage(File fileToConvert) throws IOException {
        return ImageIO.read(fileToConvert);
    }

    public PuzzleQuestion getRandomlySelectedQuestion() {
        return gameQuestions.get(pickRandomly(0, gameQuestions.size()));
    }

    public Rebus getRandomlySelectedRebus() {
        return randomlySelectedRebus;
    }

    public boolean answerPuzzleQuestion(PuzzleQuestion puzzleQuestion, String answer) {
        return answeredCorrectly(puzzleQuestion.isCorrect(answer));
    }

    private boolean answeredCorrectly(final boolean isCorrect) {
        if (isCorrect) {
            numberOfQuestionsAnsweredCorrectly++;
        } else {
            numberOfQuestionsAnsweredIncorrectly++;
        }
        return isCorrect;
    }

    public List<PuzzleQuestion> getGameQuestions() {
        return gameQuestions;
    }

    public int getNumberOfQuestionsAnsweredIncorrectly() {
        return numberOfQuestionsAnsweredIncorrectly;
    }

    public int getNumberOfQuestionsAnsweredCorrectly() {
        return numberOfQuestionsAnsweredCorrectly;
    }

    public int getNumberOfQuestionsAvailableToAnswer() {
        return numberOfQuestionsAvailableToAnswer;
    }

    private static final class PuzzleQuestions {
        private final List<PuzzleQuestion> puzzleQuestions;

        @JsonCreator
        public PuzzleQuestions(@JsonProperty("puzzleQuestions") List<PuzzleQuestion> puzzleQuestions) {
            this.puzzleQuestions = puzzleQuestions;
        }

        public List<PuzzleQuestion> getPuzzleQuestions() {
            return puzzleQuestions;
        }
    }
}
