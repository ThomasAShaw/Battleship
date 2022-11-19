package battleship;

public class Game {
    private final Board playerOne, playerTwo;
    private boolean gameStarted = false;
    private boolean gameOver = false;

    /**
     * Initializes a new Game object.
     */
    public Game() {
        this.playerOne = new Board();
        this.playerTwo = new Board();
    }

    /**
     * Have a player attempt to guess the location of another player's ship.
     * @param firstPlayerTurn the player's turn it is to guess; true if first player, false otherwise.
     * @param guessCoordinate the coordinate to guess where a ship is; not null.
     * @return a String containing one of "HIT", "MISS", or "FAIL" and any appropriate details:
     *         "HIT" attacking player, victim player, if the hit sunk a ship.
     *         "MISS" attacking player, victim player.
     *         "FAIL" reason guess failed.
     */
    public String guessLocation(boolean firstPlayerTurn, Coordinate guessCoordinate) {
        gameStarted = true;

        if (gameOver) {
            return "FAIL: Game over!";
        }

        if (firstPlayerTurn) {
            if (playerTwo.coordinateOutsideBoard(guessCoordinate)) {
                return "FAIL: Guess outside P2 board.";
            }

            int numShipsSunk = playerTwo.numShipsSunk();
            try {
                if (playerTwo.guessLocation(guessCoordinate)) {
                    if (playerTwo.numShipsSunk() > numShipsSunk) {
                        return "HIT: P1 sunk P2's ship!";
                    } else {
                        return "HIT: P1 hit P2's ship!";
                    }
                } else {
                    return "MISS: P1 did not hit P2's ship.";
                }
            } catch (CoordinateAlreadyGuessedException e) {
                return "FAIL: P1 already guessed this coordinate.";
            }
        } else {
            if (playerOne.coordinateOutsideBoard(guessCoordinate)) {
                return "FAIL: Guess outside P1 board.";
            }

            int numShipsSunk = playerOne.numShipsSunk();
            try {
                if (playerOne.guessLocation(guessCoordinate)) {
                    if (playerOne.numShipsSunk() > numShipsSunk) {
                        return "HIT: P2 sunk P1's ship!";
                    } else {
                        return "HIT: P2 hit P1's ship!";
                    }
                } else {
                    return "MISS: P2 not hit P1's ship.";
                }
            } catch (CoordinateAlreadyGuessedException e) {
                return "FAIL: P2 already guessed this coordinate.";
            }
        }
    }

    /**
     * Set a ship on a specific player's board.
     * @param firstPlayerTurn who's turn it is to place the ship.
     * @param ship the ship to place on a player's board; not null.
     * @return true if successfully placed, false otherwise.
     */
    public boolean setShip(boolean firstPlayerTurn, Ship ship) {
        if (gameStarted) {
            return false;
        }

        Ship copyShip = new Ship(ship.getName(), ship.getCoordinates());

        if (firstPlayerTurn) {
            try {
                playerOne.setShip(copyShip);
                return true;
            } catch (InvalidPlacementException e) {
                return false;
            }
        } else {
            try {
                playerTwo.setShip(copyShip);
                return true;
            } catch (InvalidPlacementException e) {
                return false;
            }
        }
    }

    /**
     * Check if the game is over.
     * @return true if game is over, false otherwise.
     */
    private boolean checkEndGame() {
        if (gameStarted && !gameOver) {
            gameOver = playerOne.allShipsSunk() || playerTwo.allShipsSunk();
        }

        return gameOver;
    }

    /**
     * Check the winner of game.
     * @return player(s) who won; "Not Ended", "Player 1", "Player 2", "Tie", or "Neither".
     */
    public String checkWinner() {
        if (gameOver || checkEndGame()) {
            boolean playerOneLoss = playerOne.allShipsSunk();
            boolean playerTwoLoss = playerTwo.allShipsSunk();
            if (playerOneLoss && playerTwoLoss) {
                return "Tie";
            } else if (playerOneLoss) {
                return "Player 2";
            } else if (playerTwoLoss) {
                return "Player 1";
            } else {
                return "Neither";
            }
        }

        return "Not Ended";
    }

    /**
     * Ends the game, allowing no more moves to be made for the game.
     */
    public void endGame() {
        this.gameOver = true;
    }
}
