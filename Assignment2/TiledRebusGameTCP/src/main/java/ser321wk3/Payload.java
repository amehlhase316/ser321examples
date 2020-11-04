package ser321wk3;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class Payload implements Serializable {
    private String base64encodedCroppedImage;
    private String message;
    private boolean wonGame;
    private boolean gameOver;

    public Payload(String message, boolean wonGame, boolean gameOver) {
        this(null, message, wonGame, gameOver);
    }

    public Payload(String base64encodedCroppedImage,
                   String message,
                   boolean wonGame,
                   boolean gameOver) {
        this.base64encodedCroppedImage = base64encodedCroppedImage;
        this.message = message;
        this.wonGame = wonGame;
        this.gameOver = gameOver;
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
