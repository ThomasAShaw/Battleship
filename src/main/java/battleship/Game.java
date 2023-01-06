package battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private final Board playerOne, playerTwo;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    public final static List<Ship> DEFAULT_SHIPS = Collections.unmodifiableList(
            List.of(new Ship("Carrier", new Coordinate(0, 0), new Coordinate(0, 4)),
                    new Ship("Battleship", new Coordinate(0, 0), new Coordinate(0, 3)),
                    new Ship("Cruiser", new Coordinate(0, 0), new Coordinate(0, 2)),
                    new Ship("Submarine", new Coordinate(0,0), new Coordinate(0,2)),
                    new Ship("Destroyer", new Coordinate(0,0), new Coordinate(0, 1)))
    );

    public final List<GameEvent> gameHistory = new ArrayList<>();

    /**
     * Initialises a new Game object, with the default board size and ships.
     */
    public Game() {
        this.playerOne = new Board();
        this.playerTwo = new Board();
        GameEvent.resetCount();
    }

    /**
     * Have a player attempt to guess the location of another player's ship.
     * @param firstPlayerTurn the player's turn it is to guess; true if first player, false otherwise.
     * @param guessCoordinate the coordinate to guess where a ship is; not null.
     * @return a String containing one of "HIT", "MISS", or "FAIL" and any appropriate details:
     *         "HIT" attacking player, victim player, if the hit sunk a ship, if attacker won.
     *         "MISS" attacking player, victim player.
     *         "FAIL" reason guess failed.
     */
    public String guessLocation(boolean firstPlayerTurn, Coordinate guessCoordinate) {
        gameStarted = true;

        if (gameOver) {
            gameHistory.add(new GameEvent(GameEventType.FAIL, guessCoordinate, firstPlayerTurn, List.of("game over")));
            return "FAIL: Game over!";
        }

        if (firstPlayerTurn) {
            if (playerTwo.coordinateOutsideBoard(guessCoordinate)) {
                gameHistory.add(new GameEvent(GameEventType.FAIL, guessCoordinate, firstPlayerTurn, List.of("outside board")));
                return "FAIL: Guess outside P2 board.";
            }

            int numShipsSunk = playerTwo.numShipsSunk();
            try {
                if (playerTwo.guessLocation(guessCoordinate)) {
                    if (playerTwo.numShipsSunk() > numShipsSunk) {
                        if (playerTwo.allShipsSunk()) { // TODO: add as test, bug here.
                            gameHistory.add(new GameEvent(GameEventType.HIT, guessCoordinate, firstPlayerTurn, List.of("win")));
                            return "HIT: P1 sunk P2's ship and won!";
                        }
                        gameHistory.add(new GameEvent(GameEventType.HIT, guessCoordinate, firstPlayerTurn, List.of("sunk")));
                        return "HIT: P1 sunk P2's ship!";
                    } else {
                        gameHistory.add(new GameEvent(GameEventType.HIT, guessCoordinate, firstPlayerTurn, List.of("")));
                        return "HIT: P1 hit P2's ship!";
                    }
                } else {
                    gameHistory.add(new GameEvent(GameEventType.MISS, guessCoordinate, firstPlayerTurn, List.of("miss")));
                    return "MISS: P1 did not hit P2's ship.";
                }
            } catch (CoordinateAlreadyGuessedException e) {
                gameHistory.add(new GameEvent(GameEventType.FAIL, guessCoordinate, firstPlayerTurn, List.of("already guessed")));
                return "FAIL: P1 already guessed this coordinate.";
            }
        } else {
            if (playerOne.coordinateOutsideBoard(guessCoordinate)) {
                gameHistory.add(new GameEvent(GameEventType.FAIL, guessCoordinate, firstPlayerTurn, List.of("outside board")));
                return "FAIL: Guess outside P1 board.";
            }

            int numShipsSunk = playerOne.numShipsSunk();
            try {
                if (playerOne.guessLocation(guessCoordinate)) {
                    if (playerOne.numShipsSunk() > numShipsSunk) {
                        if (playerOne.allShipsSunk()) {
                            gameHistory.add(new GameEvent(GameEventType.HIT, guessCoordinate, firstPlayerTurn, List.of("win")));
                            return "HIT: P2 sunk P1's ship and won!";
                        }
                        gameHistory.add(new GameEvent(GameEventType.HIT, guessCoordinate, firstPlayerTurn, List.of("sunk")));
                        return "HIT: P2 sunk P1's ship!";
                    } else {
                        gameHistory.add(new GameEvent(GameEventType.HIT, guessCoordinate, firstPlayerTurn, List.of("")));
                        return "HIT: P2 hit P1's ship!";
                    }
                } else {
                    gameHistory.add(new GameEvent(GameEventType.MISS, guessCoordinate, firstPlayerTurn, List.of("")));
                    return "MISS: P2 not hit P1's ship.";
                }
            } catch (CoordinateAlreadyGuessedException e) {
                gameHistory.add(new GameEvent(GameEventType.FAIL, guessCoordinate, firstPlayerTurn, List.of("already guessed")));
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

    /**
     * Get board width for a specific player.
     * @param isPlayerOne true if referring to first player, false for second player.
     * @return horizontal width of the specified player's board.
     */
    public int getBoardWidth(boolean isPlayerOne) {
        if (isPlayerOne) {
            return playerOne.getXSize();
        }

        return playerTwo.getXSize();
    }

    /**
     * Get board height for a specific player.
     * @param isPlayerOne true if referring to first player, false for second player.
     * @return vertical height of the specified player's board.
     */
    public int getBoardHeight(boolean isPlayerOne) {
        if (isPlayerOne) {
            return playerOne.getYSize();
        }

        return playerTwo.getYSize();
    }

    /**
     * Check if a coordinate is valid for a specific player's board.
     * @param coordinate coordinate to check; not null.
     * @param isPlayerOne true if referring to first player, false for second player.
     * @return true if the given coordinate is valid for the specified player, false otherwise.
     */
    public boolean isValidCoordinate(Coordinate coordinate, boolean isPlayerOne) {
        if (isPlayerOne) {
            return !playerOne.coordinateOutsideBoard(coordinate);
        }

        return !playerTwo.coordinateOutsideBoard(coordinate);
    }

    /**
     Get information on all ships for a specified player, placed or unplaced.
     * @param isPlayerOne true if referring to first player, false for second player.
     * @return a list of ships for the specified player, but all begin at (0,0),
     *         are oriented downwards, and are not the same ships used by this game.
     */
    public List<Ship> getAllShips(boolean isPlayerOne) {
        // FIXME: Currently only returns default ships.
        List<Ship> shipList = new ArrayList<>();
        for (Ship s : DEFAULT_SHIPS) {
            shipList.add(new Ship(s.getName(), new Coordinate(0, 0), new Coordinate(0, s.getShipLength() - 1)));
        }
        return shipList;
    }

    /**
     * Get information on all placed ships for a specified player.
     * @param isPlayerOne true if referring to first player, false for second player.
     * @return a list of ships for the specified player, with their positions on the board.
     */
    public List<Ship> getPlacedShips (boolean isPlayerOne) {
        return isPlayerOne ? playerOne.getShips() : playerTwo.getShips();
    }

    /**
     * Get all new events that have happened in the game, past a certain point.
     * @param startingEventNum event number which everything after will be returned.
     * @return all events that happened after specified point.
     */
    public List<GameEvent> getNewEvents(int startingEventNum) {
        List<GameEvent> newEvents = new ArrayList<>();

        for (int i = startingEventNum; i < gameHistory.size(); i++) {
            GameEvent event = gameHistory.get(i);

            if (event.getEventType() != GameEventType.FAIL) {
                newEvents.add(event);
            }
        }

        return newEvents;
    }
}
