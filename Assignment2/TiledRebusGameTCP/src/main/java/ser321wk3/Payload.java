package ser321wk3;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Payload {
    private String base64encodedCroppedImage;
    private String message;
    private boolean wonGame;
    private boolean gameOver;
    private boolean answerIsCorrect;

    @JsonCreator
    public Payload(@JsonProperty("base64encodedCroppedImage") String base64encodedCroppedImage,
                   @JsonProperty("message") String message,
                   @JsonProperty("wonGame") boolean wonGame,
                   @JsonProperty("gameOver") boolean gameOver,
                   @JsonProperty("answerIsCorrect") boolean answerIsCorrect) {
        this.base64encodedCroppedImage = base64encodedCroppedImage;
        this.message = message;
        this.wonGame = wonGame;
        this.gameOver = gameOver;
        this.answerIsCorrect = answerIsCorrect;
    }

    public boolean isAnswerIsCorrect() {
        return answerIsCorrect;
    }

    public void setAnswerIsCorrect(boolean answerIsCorrect) {
        this.answerIsCorrect = answerIsCorrect;
    }

    public boolean answeredCorrectly() {
        return isAnswerIsCorrect();
    }

    public String getBase64encodedCroppedImage() {
        return base64encodedCroppedImage;
    }

    public void setBase64encodedCroppedImage(String base64encodedCroppedImage) {
        this.base64encodedCroppedImage = base64encodedCroppedImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean wonGame() {
        return isWonGame();
    }

    public boolean gameOver() {
        return isGameOver();
    }

    public boolean isWonGame() {
        return wonGame;
    }

    public void setWonGame(boolean wonGame) {
        this.wonGame = wonGame;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
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

        Payload payload = (Payload) o;

        return new EqualsBuilder()
                .append(wonGame, payload.wonGame)
                .append(gameOver, payload.gameOver)
                .append(base64encodedCroppedImage, payload.base64encodedCroppedImage)
                .append(message, payload.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(base64encodedCroppedImage)
                .append(message)
                .append(wonGame)
                .append(gameOver)
                .toHashCode();
    }
}
