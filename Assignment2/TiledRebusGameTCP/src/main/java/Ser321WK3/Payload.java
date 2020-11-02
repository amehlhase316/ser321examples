package Ser321WK3;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;


public class Payload implements Serializable {
    private List<byte[]> croppedImages;
    private String message;
    private boolean wonGame;
    private boolean gameOver;

    public Payload(String message, boolean wonGame, boolean gameOver) {
        this(null, message, wonGame, gameOver);
    }

    public Payload(List<byte[]> croppedImages,
                   String message,
                   boolean wonGame,
                   boolean gameOver) {
        this.croppedImages = croppedImages;
        this.message = message;
        this.wonGame = wonGame;
        this.gameOver = gameOver;
    }


    public List<byte[]> getCroppedImages() {
        return croppedImages;
    }

    public void setCroppedImages(List<byte[]> croppedImages) {
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
