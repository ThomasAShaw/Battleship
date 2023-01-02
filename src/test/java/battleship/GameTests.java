package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    @Test
    public void testSettingShips() {
        Ship shipA = new Ship("My Cool Ship", new Coordinate(1,2), new Coordinate(7,2));
        Ship shipB = new Ship("My Cool Ship", new Coordinate(1, 3), new Coordinate(1, 5));
        Ship shipC = new Ship("My Cool Overlapping Ship", new Coordinate(0,4), new Coordinate(9, 4));
        Ship shipD = new Ship("outside the board ship", new Coordinate(5,5), new Coordinate(10, 5));

        Game testGame = new Game();

        assertTrue(testGame.setShip(true, shipA));
        assertTrue(testGame.setShip(false, shipA));

        assertFalse(testGame.setShip(true, shipA));
        assertFalse(testGame.setShip(true, shipA));

        assertTrue(testGame.setShip(true, shipB));

        assertFalse(testGame.setShip(true, shipC));
        assertTrue(testGame.setShip(false, shipC));

        assertFalse(testGame.setShip(true, shipD));
        assertFalse(testGame.setShip(false, shipD));
    }

    @Test
    public void testShipCoordsSetting() {
        Ship shipA = new Ship("a valid ship", new Coordinate(6, 3), new Coordinate(6, 1));
        Ship shipB = new Ship("overlapping ship", new Coordinate(5, 1), new Coordinate(6, 1));
        Ship shipC = new Ship("ship outside board", new Coordinate(2, 8), new Coordinate(-1, 8));
        Ship shipD = new Ship("another valid ship", new Coordinate(1, 6), new Coordinate(1, 9));
        Ship shipE = new Ship("a ship exclusive to player one", new Coordinate(0, 0), new Coordinate(0,0));

        Game testGame = new Game();

        assertTrue(testGame.setShip(true, shipA));
        assertTrue(testGame.setShip(false, shipA));
        assertFalse(testGame.setShip(true, shipB));
        assertFalse(testGame.setShip(false, shipB));
        assertFalse(testGame.setShip(true, shipC));
        assertFalse(testGame.setShip(false, shipC));
        assertTrue(testGame.setShip(true, shipD));
        assertTrue(testGame.setShip(false, shipD));
        assertTrue(testGame.setShip(true, shipE));

        // Testing coords of shipA were actually set.
        assertTrue(testGame.guessLocation(true, new Coordinate(6,3)).contains("HIT"));
        assertTrue(testGame.guessLocation(true, new Coordinate(6,1)).contains("HIT"));
        assertTrue(testGame.guessLocation(true, new Coordinate(6,2)).contains("sunk"));

        assertTrue(testGame.guessLocation(false, new Coordinate(6,1)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(6,2)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(6,3)).contains("sunk"));

        // Testing no coords of shipB were actually set.
        assertTrue(testGame.guessLocation(true, new Coordinate(6,1)).contains("FAIL"));
        assertTrue(testGame.guessLocation(true, new Coordinate(5,1)).contains("MISS"));

        assertTrue(testGame.guessLocation(false, new Coordinate(5,1)).contains("MISS"));
        assertTrue(testGame.guessLocation(false, new Coordinate(6,1)).contains("FAIL"));

        // Testing no coords of shipC were actually set, but (1, 8) is a coord of shipD.
        assertTrue(testGame.guessLocation(true, new Coordinate(-1,8)).contains("FAIL"));
        assertTrue(testGame.guessLocation(true, new Coordinate(1,8)).contains("HIT"));
        assertTrue(testGame.guessLocation(true, new Coordinate(2,8)).contains("MISS"));
        assertTrue(testGame.guessLocation(true, new Coordinate(0,8)).contains("MISS"));

        assertTrue(testGame.guessLocation(false, new Coordinate(1,8)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(0,8)).contains("MISS"));
        assertTrue(testGame.guessLocation(false, new Coordinate(2,8)).contains("MISS"));
        assertTrue(testGame.guessLocation(false, new Coordinate(-1,8)).contains("FAIL"));

        // Testing coords of shipD were actually set.
        assertTrue(testGame.guessLocation(true, new Coordinate(1,7)).contains("HIT"));
        assertTrue(testGame.guessLocation(true, new Coordinate(1,6)).contains("HIT"));
        assertTrue(testGame.guessLocation(true, new Coordinate(1,9)).contains("sunk"));

        assertTrue(testGame.guessLocation(false, new Coordinate(1,6)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(1,9)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(1,7)).contains("sunk"));

        // Testing shipE is exclusive to playerOne.
        assertFalse(testGame.setShip(false, shipE));
        assertTrue(testGame.guessLocation(true, new Coordinate(0,0)).contains("MISS"));
        assertTrue(testGame.guessLocation(false, new Coordinate(0,0)).contains("sunk"));
    }

    @Test
    public void testShipGuessing() {
        Ship shipA1 = new Ship("Ship A1", new Coordinate(9,9), new Coordinate(9,7));
        Ship shipA2 = new Ship("Ship A2", new Coordinate(7, 3), new Coordinate(5,3));
        Ship shipB1 = new Ship("Ship B1", new Coordinate(3,1), new Coordinate(5,1));
        Ship shipB2 = new Ship("Ship B2", new Coordinate(0, 0), new Coordinate(0,2));

        Game testGame = new Game();
        testGame.setShip(true, shipA1);
        testGame.setShip(true, shipA2);
        testGame.setShip(false, shipB1);
        testGame.setShip(false, shipB2);

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(0, 0)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(0, 0)).contains("MISS"));

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(9, 8)).contains("MISS"));
        assertTrue(testGame.guessLocation(false, new Coordinate(9, 8)).contains("HIT"));

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(0, 2)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(0, 2)).contains("MISS"));

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(0, 1)).contains("sunk"));
        assertTrue(testGame.guessLocation(false, new Coordinate(0, 1)).contains("MISS"));

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(3, 1)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(3, 1)).contains("MISS"));

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(4, 3)).contains("MISS"));
        assertTrue(testGame.guessLocation(false, new Coordinate(4, 3)).contains("MISS"));

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(0, 1)).contains("FAIL"));
        assertTrue(testGame.guessLocation(false, new Coordinate(0, 1)).contains("FAIL"));

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(4, 1)).contains("HIT"));
        assertTrue(testGame.guessLocation(false, new Coordinate(4, 1)).contains("MISS"));

        assertTrue(testGame.checkWinner().contains("Not Ended"));
        assertTrue(testGame.guessLocation(true, new Coordinate(5, 1)).contains("sunk"));
        assertTrue(testGame.guessLocation(false, new Coordinate(5, 1)).contains("MISS"));

        assertTrue(testGame.checkWinner().contains("Player 1"));
        assertTrue(testGame.guessLocation(true, new Coordinate(3, 3)).contains("FAIL"));
        assertTrue(testGame.guessLocation(false, new Coordinate(3, 3)).contains("FAIL"));
    }

    @Test
    public void testWinners() {
        Coordinate coordToGuess = new Coordinate(2, 5);
        Ship oneUnitShip = new Ship("baby ship", coordToGuess, coordToGuess);

        Game playerOneWin = new Game();
        Game playerTwoWin = new Game();
        Game tiedGame = new Game();
        Game noWinners = new Game();

        playerOneWin.setShip(true, oneUnitShip);
        playerOneWin.setShip(false, oneUnitShip);
        playerTwoWin.setShip(true, oneUnitShip);
        playerTwoWin.setShip(false, oneUnitShip);
        tiedGame.setShip(true, oneUnitShip);
        tiedGame.setShip(false, oneUnitShip);
        noWinners.setShip(true, oneUnitShip);
        noWinners.setShip(false, oneUnitShip);

        assertTrue(playerOneWin.checkWinner().contains("Not Ended"));
        assertTrue(playerTwoWin.checkWinner().contains("Not Ended"));
        assertTrue(tiedGame.checkWinner().contains("Not Ended"));
        assertTrue(noWinners.checkWinner().contains("Not Ended"));

        playerOneWin.guessLocation(true, coordToGuess);
        assertTrue(playerOneWin.checkWinner().contains("Player 1"));
        assertTrue(playerTwoWin.checkWinner().contains("Not Ended"));
        assertTrue(tiedGame.checkWinner().contains("Not Ended"));
        assertTrue(noWinners.checkWinner().contains("Not Ended"));

        playerTwoWin.guessLocation(false, coordToGuess);
        assertTrue(playerOneWin.checkWinner().contains("Player 1"));
        assertTrue(playerTwoWin.checkWinner().contains("Player 2"));
        assertTrue(tiedGame.checkWinner().contains("Not Ended"));
        assertTrue(noWinners.checkWinner().contains("Not Ended"));

        tiedGame.guessLocation(true, coordToGuess);
        tiedGame.guessLocation(false, coordToGuess);
        assertTrue(playerOneWin.checkWinner().contains("Player 1"));
        assertTrue(playerTwoWin.checkWinner().contains("Player 2"));
        assertTrue(tiedGame.checkWinner().contains("Tie"));
        assertTrue(noWinners.checkWinner().contains("Not Ended"));

        noWinners.endGame();
        assertTrue(playerOneWin.checkWinner().contains("Player 1"));
        assertTrue(playerTwoWin.checkWinner().contains("Player 2"));
        assertTrue(tiedGame.checkWinner().contains("Tie"));
        assertTrue(noWinners.checkWinner().contains("Neither"));

        assertTrue(playerOneWin.guessLocation(false, coordToGuess).contains("FAIL"));
        assertTrue(playerTwoWin.guessLocation(true, coordToGuess).contains("FAIL"));
        assertTrue(playerOneWin.checkWinner().contains("Player 1"));
        assertTrue(playerTwoWin.checkWinner().contains("Player 2"));
        assertTrue(tiedGame.checkWinner().contains("Tie"));
        assertTrue(noWinners.checkWinner().contains("Neither"));
    }
}
