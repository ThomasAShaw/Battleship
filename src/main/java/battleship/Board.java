package battleship;

import java.util.*;

public class Board {
    private final Coordinate[][] boardMatrix;
    private final Set<Ship> shipManager = new HashSet<>();
    public static final int DEFAULT_SIZE = 10;

    /**
     * Initialises a new board object with the default 10 x 10 layout.
     */
    public Board() {
        this.boardMatrix = new Coordinate[DEFAULT_SIZE][DEFAULT_SIZE];

        /* Initialize boardMatrix. */
        for (int y = 0; y < boardMatrix.length; y++) {
            for (int x = 0; x < boardMatrix[y].length; x++) {
                boardMatrix[y][x] = new Coordinate(x, y);
            }
        }
    }

    /**
     * Place a ship on the board.
     * @param ship the ship to place on the board; not null.
     * @throws InvalidPlacementException when attempting to place ship in invalid location.
     */
    public void setShip(Ship ship) throws InvalidPlacementException {
        /* Check all coordinates not occupied and inside boardMatrix. */
        List<Coordinate> shipCoordinates = ship.getCoordinates();
        List<Coordinate> boardCoordinates = new ArrayList<>();

        for (Coordinate c : shipCoordinates) {
            if (coordinateOutsideBoard(c)) {
                throw new InvalidPlacementException("Ship coordinate(s) outside of board area.");
            }

            if (boardMatrix[c.getY()][c.getX()].isOccupied()) {
                throw new InvalidPlacementException("Coordinate(s) already occupied by another ship.");
            }

            if (boardMatrix[c.getY()][c.getX()].isGuessed()) {
                throw new InvalidPlacementException("Coordinate(s) already previously guessed.");
            }
        }

        /* Gather all coordinates for new ship, only after checking no exceptions. */
        for (Coordinate c : shipCoordinates) {
            boardCoordinates.add(boardMatrix[c.getY()][c.getX()]);
        }
        Ship shipToAdd = new Ship(ship.getName(), boardCoordinates);

        /* Now set the coordinates to be linked to the ship. */
        for (Coordinate c : shipToAdd.getCoordinates()) {
            boardMatrix[c.getY()][c.getX()] = c;
        }

        shipManager.add(shipToAdd);
    }

    /**
     * Guess a location on the board to attempt to hit a ship.
     * @param guessCoordinate coordinate to guess; not null.
     * @return true if guess successfully hit a ship, false otherwise.
     * @throws CoordinateAlreadyGuessedException if location was already guessed.
     */
    public boolean guessLocation(Coordinate guessCoordinate) throws CoordinateAlreadyGuessedException {
        if (guessCoordinate == null) {
            throw new IllegalArgumentException("Coordinate cannot be null.");
        } else if (coordinateOutsideBoard(guessCoordinate)) {
            throw new IllegalArgumentException("Coordinate outside of board area.");
        }

        Coordinate guessedCoordinates = boardMatrix[guessCoordinate.getY()][guessCoordinate.getX()];
        return guessedCoordinates.guessCoordinate();
    }

    /**
     * Gets the total number of ships on the board.
     * @return the total number of ships on the board.
     */
    public int numShips() {
        return shipManager.size();
    }

    /**
     * Gets the total number of ships on the board.
     * @return the total number of ships on the board.
     */
    public int numShipsSunk() {
        int numSunk = 0;
        for (Ship s : shipManager) {
            if (s.isSunk()) {
                numSunk++;
            }
        }

        return numSunk;
    }

    /**
     * Check if all ships on this board have been sunk.
     * @return true if all ships are sunk, false otherwise.
     */
    public boolean allShipsSunk() {
        return numShipsSunk() == numShips();
    }

    /**
     * Check if a coordinate is outside the board.
     * @param coordinate coordinate to check; not null.
     * @return true if outside board and therefore invalid, false otherwise.
     */
    public boolean coordinateOutsideBoard(Coordinate coordinate) {
        return coordinate.getY() >= boardMatrix.length || coordinate.getY() < 0
                || coordinate.getX() >= boardMatrix[coordinate.getY()].length || coordinate.getX() < 0;
    }

    /**
     Get information on a coordinate at the position (x,y) on the board.
     * @param x horizontal position of coordinate on board.
     * @param y vertical position of coordinate on board.
     * @return a duplicated coordinate at the specified position, but does not have the same occupying ship.
     * @throws InvalidPlacementException when attempting to access coordinate in invalid location.
     */
    public Coordinate getCoordinate(int x, int y) throws InvalidPlacementException {
        if (coordinateOutsideBoard(new Coordinate(x, y))) {
            throw new InvalidPlacementException("Coordinate outside of board area.");
        }

        /* Occupying ship is different for duplicatedCoordinate than boardCoordinate to avoid accidental modification. */
        Coordinate boardCoordinate = boardMatrix[y][x];
        Coordinate duplicatedCoordinate = new Coordinate(boardCoordinate.getX(), boardCoordinate.getY(), boardCoordinate.isGuessed(), null);

        if (boardCoordinate.isOccupied()) {
            duplicatedCoordinate.setShip(new Ship(boardCoordinate.getOccupyingShip().getName(), duplicatedCoordinate, duplicatedCoordinate));
        }

        return duplicatedCoordinate;
    }

    /**
     * Reset a specified coordinate to default values.
     * @param x horizontal position of coordinate on board.
     * @param y vertical position of coordinate on board.
     * @throws InvalidPlacementException when attempting to access coordinate in invalid location.
     */
    public void resetCoordinate(int x, int y) throws InvalidPlacementException {
        if (coordinateOutsideBoard(new Coordinate(x, y))) {
            throw new InvalidPlacementException("Coordinate outside of board area.");
        }

        boardMatrix[y][x] = new Coordinate(x, y);
    }

    /**
     * @return horizontal size of board.
     */
    public int getXSize() {
        return boardMatrix[0].length;
    }

    /**
     * @return vertical size of board.
     */
    public int getYSize() {
        return boardMatrix.length;
    }
}
