package Ser321WK3;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.awt.image.BufferedImage;
import java.util.List;


public class Payload {
    private final List<BufferedImage> croppedImages;
    private final String message;
    private final boolean wonGame;
    private final boolean gameOver;

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

    public boolean wonGame() {
        return wonGame;
    }

    public boolean gameOver() {
        return gameOver;
    }

    public List<BufferedImage> getCroppedImages() {
        return croppedImages;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
