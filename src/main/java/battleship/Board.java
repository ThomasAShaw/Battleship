package battleship;

import java.util.*;

public class Board {
    private Coordinate[][] boardMatrix;
    private Set<Ship> shipManager = new HashSet<>();

    /**
     * Initialises a new board object with the default 10 x 10 layout.
     */
    public Board() {
        this.boardMatrix = new Coordinate[10][10];

        /* Initialize boardMatrix. */
        for (int y = 0; y < boardMatrix.length; y++) {
            for (int x = 0; x < boardMatrix[y].length; x++) {
                boardMatrix[y][x] = new Coordinate(x, y);
            }
        }
    }

    /**
     * Place a ship on the board.
     * @param ship the battleship.Ship to place on the board; not null.
     * @throws InvalidPlacementException when attempting to place ship in invalid location.
     */
    public void setShip(Ship ship) throws InvalidPlacementException {
        /* Check all coordinates not occupied and inside boardMatrix. */
        List<Coordinate> shipCoords = ship.getCoordinates();
        List<Coordinate> boardCoords = new ArrayList<>();

        for (Coordinate c : shipCoords) {
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
        for (Coordinate c : shipCoords) {
            boardCoords.add(boardMatrix[c.getY()][c.getX()]);
        }
        Ship shipToAdd = new Ship(ship.getName(), boardCoords);

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

        Coordinate guessedCoord = boardMatrix[guessCoordinate.getY()][guessCoordinate.getX()];
        return guessedCoord.guessCoordinate();
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
        if (numShipsSunk() == numShips()) {
            return true;
        }

        return false;
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
}
