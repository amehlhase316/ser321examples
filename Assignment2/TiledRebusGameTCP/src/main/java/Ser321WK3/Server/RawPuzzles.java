package Ser321WK3.Server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RawPuzzles {
    private final List<RawPuzzle> puzzles;

    @JsonCreator
    public RawPuzzles(@JsonProperty("puzzles") List<RawPuzzle> puzzles) {
        this.puzzles = puzzles;
    }

    public List<RawPuzzle> getPuzzles() {
        return puzzles;
    }
}
