package Ser321WK3;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.awt.image.BufferedImage;
import java.util.List;


public class Payload {
    private List<BufferedImage> croppedImages;
    private String message;
    private boolean wonGame;
    private boolean gameOver;

    public Payload(String message, boolean wonGame, boolean gameOver) {
        this(null, message, wonGame, gameOver);
    }

    @JsonCreator
    public Payload(@JsonProperty("croppedImages") List<BufferedImage> croppedImages,
                   @JsonProperty("message") String message,
                   @JsonProperty("wonGame") boolean wonGame,
                   @JsonProperty("gameOver") boolean gameOver) {
        this.croppedImages = croppedImages;
        this.message = message;
        this.wonGame = wonGame;
        this.gameOver = gameOver;
    }


    public List<BufferedImage> getCroppedImages() {
        return croppedImages;
    }

    public void setCroppedImages(List<BufferedImage> croppedImages) {
        this.croppedImages = croppedImages;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isWonGame() {
        return wonGame;
    }

    public void setWonGame(boolean wonGame) {
        this.wonGame = wonGame;
    }

    public boolean gameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
