package Server;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Objects;

public class PuzzleQuestion {
    private final String question;
    private final String answer;

    @JsonCreator
    public PuzzleQuestion(@JsonProperty("question") String question,
                          @JsonProperty("answer") String answer) {
        Objects.requireNonNull(question);
        Objects.requireNonNull(answer);
        this.question = question;
        this.answer = stripSpacesAndPunctuation(answer);
    }

    private String stripSpacesAndPunctuation(String stringWithSpacesAndPuncuation) {
        return stringWithSpacesAndPuncuation.replaceAll("[^A-Za-z\\d]", "").toLowerCase();
    }

    public boolean isCorrect(String puzzleQuestionUserGuess) {
        puzzleQuestionUserGuess = (puzzleQuestionUserGuess == null ? "" : puzzleQuestionUserGuess);
        return this.answer.equals(stripSpacesAndPunctuation(puzzleQuestionUserGuess));
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
