import java.util.*;

public class Board {
    private Coordinate[][] boardMatrix;
    private Set<Ship> shipManager = new HashSet<>();

    /**
     * Initialises a new Board object with the default 10 x 10 layout.
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
     * Place a Ship on the board.
     * @param ship the Ship to place on the board; not null.
     * @throws InvalidPlacementException when attempting to place ship in invalid location.
     */
    public void setShip(Ship ship) throws InvalidPlacementException {
        /* Check all coordinates not occupied and inside boardMatrix. */
        List<Coordinate> shipCoords = ship.getCoordinates();
        List<Coordinate> boardCoords = new ArrayList<>();

        for (Coordinate c : shipCoords) {
            if (c.getY() > boardMatrix.length || c.getY() < 0
                    || c.getX() > boardMatrix[c.getY()].length || c.getX() < 0) {
                throw new InvalidPlacementException("Ship coordinate(s) outside of board area.");
            }

            if (boardMatrix[c.getY()][c.getY()].isOccupied()) {
                throw new InvalidPlacementException("Already occupied by another ship.");
            }
        }

        /* Set all coordinates to be appropriate, only after checking no exceptions. */
        for (Coordinate c : shipCoords) {
            boardMatrix[c.getY()][c.getX()].setShip(ship);
            boardCoords.add(boardMatrix[c.getY()][c.getX()]);
        }

        shipManager.add(new Ship(ship.getName(), boardCoords));
    }

    /**
     * Guess a location on the board to attempt to hit a ship.
     * @param coord coordinate to guess; not null.
     * @return true if guess successfully hit a ship, false otherwise.
     * @throws LocationAlreadyGuessedException if location was already guessed.
     */
    public boolean guessLocation(Coordinate coord) throws LocationAlreadyGuessedException {
        Coordinate guessedCoord = boardMatrix[coord.getY()][coord.getX()];

        if (guessedCoord.isGuessed()) {
            throw new LocationAlreadyGuessedException();
        }

        guessedCoord.guessCoordinate();

        return guessedCoord.isOccupied();
    }
}
