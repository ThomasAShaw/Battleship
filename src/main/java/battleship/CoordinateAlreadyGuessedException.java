package battleship;

public class CoordinateAlreadyGuessedException extends Exception {
    public CoordinateAlreadyGuessedException() {
        super("Coordinate has already been guessed.");
    }
}
