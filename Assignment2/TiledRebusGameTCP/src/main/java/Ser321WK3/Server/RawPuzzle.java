package Ser321WK3.Server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RawPuzzle {
    private final String fileName;
    private final String answer;

    @JsonCreator
    public RawPuzzle(@JsonProperty("fileName") String fileName,
                     @JsonProperty("answer") String answer) {
        this.fileName = fileName;
        this.answer = answer;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAnswer() {
        return answer;
    }
}
