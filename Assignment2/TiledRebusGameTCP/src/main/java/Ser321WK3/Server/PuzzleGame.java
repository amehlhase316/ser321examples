package Ser321WK3.Server;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class PuzzleGame {

    private static final String PUZZLE_QUESTIONS_FILE_PATH = "gameSetupFiles/PuzzleQuestions.json";
    private static final List<File> imageFiles = getRebusImageFiles();
    private final List<PuzzleQuestion> gameQuestions;
    private final int numberOfQuestionsAvailableToAnswer;
    private final Rebus randomlySelectedRebus;
    private int numberOfQuestionsAnsweredIncorrectly;
    private int numberOfQuestionsAnsweredCorrectly;

    public PuzzleGame(List<PuzzleQuestion> gameQuestions, int numberOfQuestionsAvailableToAnswer) throws IOException {
        this.gameQuestions = gameQuestions;
        this.numberOfQuestionsAvailableToAnswer = numberOfQuestionsAvailableToAnswer;
        File randomlySelected = imageFiles.get(pickRandomly(0, imageFiles.size()));
        this.randomlySelectedRebus = new Rebus(convertFileToImage(randomlySelected), randomlySelected.getName());
    }

    public PuzzleGame(int numberOfQuestionsAvailableToAnswer) throws IOException {
        this(parsePuzzleQuestions(), numberOfQuestionsAvailableToAnswer);
    }

    public static List<File> getImageFiles() {
        return imageFiles;
    }

    private static List<File> getRebusImageFiles() {
        return Arrays.stream(new File("gameSetupFiles").listFiles())
                .filter(file -> file.getName().contains(".jpg") || file.getName().contains(".png"))
                .collect(Collectors.toList());
    }

    private static List<PuzzleQuestion> parsePuzzleQuestions() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(Paths.get(PUZZLE_QUESTIONS_FILE_PATH).toFile(), PuzzleQuestions.class).getPuzzleQuestions();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(-1);
        return Collections.emptyList();
    }

    public static int pickRandomly(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public PuzzleQuestion getRandomlySelectedQuestion() {
        return gameQuestions.get(pickRandomly(0, gameQuestions.size()));
    }

    public Rebus getRandomlySelectedRebus() {
        return randomlySelectedRebus;
    }

    private BufferedImage convertFileToImage(File fileToConvert) throws IOException {
        return ImageIO.read(fileToConvert);
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
