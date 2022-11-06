public class LocationAlreadyGuessedException extends Exception {
    public LocationAlreadyGuessedException() {
        super("Location has already been guessed.");
    }
}
