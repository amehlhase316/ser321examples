package ser321wk3;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;


public class Payload implements Serializable {
    private byte[] croppedImage;
    private String message;
    private boolean wonGame;
    private boolean gameOver;

    public Payload(String message, boolean wonGame, boolean gameOver) {
        this(null, message, wonGame, gameOver);
    }

    public Payload(byte[] croppedImage,
                   String message,
                   boolean wonGame,
                   boolean gameOver) {
        this.croppedImage = croppedImage;
        this.message = message;
        this.wonGame = wonGame;
        this.gameOver = gameOver;
    }


    public byte[] getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(byte[] croppedImage) {
        this.croppedImage = croppedImage;
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
