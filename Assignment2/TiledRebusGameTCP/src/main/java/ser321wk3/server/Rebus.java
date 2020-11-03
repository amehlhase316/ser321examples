package ser321wk3.server;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.awt.image.BufferedImage;

public class Rebus {

    private final BufferedImage rebusImage;
    private final String rebusAnswer;

    public Rebus(BufferedImage rebusImage, String rebusAnswer) {
        this.rebusImage = rebusImage;
        this.rebusAnswer = parseRebusAnswer(rebusAnswer);
    }

    public static String stripSpacesAndPunctuation(String stringWithSpacesAndPunctuation) {
        return stringWithSpacesAndPunctuation.replaceAll("[^A-Za-z\\d]", "").toLowerCase();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    private String parseRebusAnswer(String rebusAnswer) {
        return stripSpacesAndPunctuation(String.join(" ", rebusAnswer.split("-")));
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
