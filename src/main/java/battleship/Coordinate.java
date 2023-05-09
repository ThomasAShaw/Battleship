package battleship;

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
     * Initialises a new Coordinate object, but with more specifications.
     * @param x the horizontal position.
     * @param y the vertical position.
     * @param isGuessed whether this coordinate has been guessed or not.
     * @param occupyingShip the ship occupying this coordinate; null if no ship is.
     *
     */
    public Coordinate(int x, int y, boolean isGuessed, Ship occupyingShip) {
        this.x = x;
        this.y = y;
        this.guessed = isGuessed;
        this.occupyingShip = occupyingShip;
    }

    /**
     * Set the ship that is occupying this coordinate.
     * @param ship ship that is occupying this coordinate.
     * @return true if was successful; false if unsuccessful.
     */
    public boolean setShip(Ship ship) {
        /* Occupying ship can only be set once, otherwise doesn't change. */
        if (isOccupied()) {
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
     * Provides the coordinate's occupied-value.
     * @return whether the coordinate is occupied by a ship already or not.
     */
    public boolean isOccupied() {
        return occupyingShip != null;
    }

    /**
     * Returns a copy of the occupying ship.
     * @return a copy of the ship occupying this coordinate.
     */
    public Ship getOccupyingShip() {
        if (occupyingShip != null) {
            return new Ship(occupyingShip.getName(), occupyingShip.getCoordinates());
        }
        return null;
    }

    /**
     * Guesses this coordinate and attempts to hit a ship.
     * @return true if it was a successful hit; false if unsuccessful.
     */
    public boolean guessCoordinate() throws CoordinateAlreadyGuessedException {
        if (guessed) {
            throw new CoordinateAlreadyGuessedException();
        }

        guessed = true;

        return occupyingShip != null;
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

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
