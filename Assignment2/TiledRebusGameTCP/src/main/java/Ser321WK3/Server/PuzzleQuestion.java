package Ser321WK3.Server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PuzzleQuestion that = (PuzzleQuestion) o;

        return new EqualsBuilder()
                .append(question, that.question)
                .append(answer, that.answer)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(question)
                .append(answer)
                .toHashCode();
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
