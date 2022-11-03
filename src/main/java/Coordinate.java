import java.util.Objects;

public class Coordinate {
    private final int x;
    private final int y;
    private boolean guessed;
    private Ship occupyingShip = null;

    /**
     * Initialises a new Coordinate object.
     * @param x the horizontal position.
     * @param y the vertical position.
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.guessed = false;
    }

    /**
     * Set the Ship that is occupying this coordinate.
     * @param ship Ship that is occupying this coordinate.
     * @return true if was successful; false if unsuccessful.
     */
    public boolean setShip(Ship ship) {
        /* Occupying ship can only be set once, otherwise doesn't change. */
        if (occupyingShip == null) {
            return false;
        }

        this.occupyingShip = ship;
        return true;
    }

    /**
     * Provides the coordinate's horizontal x-value.
     * @return the coordinate's x-value.
     */
    public int getX() {
        return x;
    }

    /**
     * Provides the coordinate's vertical y-value.
     * @return the coordinate's y-value.
     */
    public int getY() {
        return y;
    }

    /**
     * Provides the coordinate's guessed-value.
     * @return whether the coordinate has been guessed or not.
     */
    public boolean isGuessed() {
        return guessed;
    }

    /**
     * Guesses this coordinate and attempts to hit a Ship.
     * @return true if it was a successful hit; false if unsuccessful.
     */
    public boolean guessCoordinate() {
        guessed = true;
        if (occupyingShip != null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Coordinate objCoordinate = (Coordinate) obj;
        return (this.x == objCoordinate.x && this.y == objCoordinate.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
