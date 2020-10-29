package Server;

import java.awt.image.BufferedImage;

public class Rebus {

    private final BufferedImage rebusImage;
    private final String rebusAnswer;

    public Rebus(BufferedImage rebusImage, String rebusAnswer) {
        this.rebusImage = rebusImage;
        this.rebusAnswer = parseRebusAnswer(rebusAnswer);
    }

    private String parseRebusAnswer(String rebusAnswer) {
        return stripSpacesAndPunctuation(String.join(" ", rebusAnswer.split("-")));
    }

    private String stripSpacesAndPunctuation(String stringWithSpacesAndPuncuation) {
        return stringWithSpacesAndPuncuation.replaceAll("[^A-Za-z\\d]", "").toLowerCase();
    }

    public boolean isCorrect(String rebusAnswerUserGuess) {
        return this.rebusAnswer.equals(stripSpacesAndPunctuation(rebusAnswerUserGuess));
    }

    public BufferedImage getRebusImage() {
        return rebusImage;
    }

    public String getRebusAnswer() {
        return rebusAnswer;
    }
}
