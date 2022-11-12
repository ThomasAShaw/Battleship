package battleship;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTests {

    @Test
    public void testBoardConstructor() throws CoordinateAlreadyGuessedException {
        Board myBoard = new Board();
        Ship shipA = new Ship("Destroyer", new Coordinate(1, 2), new Coordinate(1, 5));
        Ship shipB = new Ship("Submarine", new Coordinate(0,0), new Coordinate(0, 3));

        try {
            myBoard.setShip(shipA);
            myBoard.setShip(shipB);
        } catch (InvalidPlacementException e) {
            fail();
        }

        assertTrue(myBoard.guessLocation(new Coordinate(1, 4)));
        assertThrows(CoordinateAlreadyGuessedException.class, () -> myBoard.guessLocation(new Coordinate(1, 4)));
    }

    @Test
    public void testSinkingShip() throws CoordinateAlreadyGuessedException {
        Board myBoard = new Board();
        Ship shipA = new Ship("Soon to be gone Ship", new Coordinate(6, 6), new Coordinate(6, 9));

        try {
            myBoard.setShip(shipA);
        } catch (InvalidPlacementException e) {
            fail();
        }

        assertEquals(0, myBoard.numShipsSunk());
        assertTrue(myBoard.guessLocation(new Coordinate(6, 6)));
        assertEquals(0, myBoard.numShipsSunk());
        assertTrue(myBoard.guessLocation(new Coordinate(6, 7)));
        assertEquals(0, myBoard.numShipsSunk());
        assertTrue(myBoard.guessLocation(new Coordinate(6, 8)));
        assertEquals(0, myBoard.numShipsSunk());
        assertTrue(myBoard.guessLocation(new Coordinate(6, 9)));
        assertEquals(1, myBoard.numShipsSunk());
        assertTrue(myBoard.allShipsSunk());
    }

    @Test
    public void testSettingImproperShips() throws CoordinateAlreadyGuessedException {
        Board myBoardA = new Board();
        Board myBoardB = new Board();

        Ship shipA = new Ship("A QUITE BIG SHIP", new Coordinate(5, 1), new Coordinate(5, 9));
        Ship shipB = new Ship("a quite small ship", new Coordinate(4, 4), new Coordinate(4,4 ));
        Ship shipC = new Ship("Overlapping ship", new Coordinate(3, 2), new Coordinate(5, 2));
        Ship shipD = new Ship("Another overlapping ship", new Coordinate(8, 4), new Coordinate(2, 4));
        Ship shipE = new Ship("A ship on a guessed location", new Coordinate(9,7), new Coordinate(9, 9));
        Ship shipF = new Ship("A ship outside the board", new Coordinate(10, 11), new Coordinate(10, 9));

        assertDoesNotThrow(() -> myBoardA.setShip(shipA));
        assertDoesNotThrow(() -> myBoardA.setShip(shipB));
        assertThrows(InvalidPlacementException.class, () -> myBoardA.setShip(shipC));
        assertThrows(InvalidPlacementException.class, () -> myBoardA.setShip(shipD));
        assertDoesNotThrow(() -> myBoardA.setShip(shipE));
        assertThrows(InvalidPlacementException.class, () -> myBoardA.setShip(shipF));

        assertDoesNotThrow(() -> myBoardB.setShip(shipA));
        assertDoesNotThrow(() -> myBoardB.setShip(shipB));
        myBoardB.guessLocation(new Coordinate(9, 8));
        assertThrows(InvalidPlacementException.class, () -> myBoardA.setShip(shipE));
        assertThrows(InvalidPlacementException.class, () -> myBoardB.setShip(shipA));
        assertThrows(InvalidPlacementException.class, () -> myBoardB.setShip(shipB));
    }

    @Test
    public void testGuessingCoordsProperly() {
        Board myBoard = new Board();
        Ship myShipA = new Ship("First Ship", new Coordinate(2, 1), new Coordinate(1,1));
        Ship myShipB = new Ship("Second Ship", new Coordinate(7, 0), new Coordinate(7, 0));
        Ship myShipC = new Ship("Third Ship", new Coordinate(9, 9), new Coordinate(8,9));

        assertDoesNotThrow(() -> myBoard.setShip(myShipA));
        assertDoesNotThrow(() -> myBoard.setShip(myShipB));
        assertDoesNotThrow(() -> myBoard.setShip(myShipC));

        try {
            assertEquals(0,myBoard.numShipsSunk());
            assertTrue(myBoard.guessLocation(new Coordinate(2,1)));
            assertTrue(myBoard.guessLocation(new Coordinate(8,9)));
            assertFalse(myBoard.guessLocation(new Coordinate(0,7)));

            assertEquals(0,myBoard.numShipsSunk());
            assertTrue(myBoard.guessLocation(new Coordinate(7,0)));
            assertEquals(1,myBoard.numShipsSunk());

            assertFalse(myBoard.guessLocation(new Coordinate(6,2)));
            assertFalse(myBoard.guessLocation(new Coordinate(3,3)));
            assertFalse(myBoard.guessLocation(new Coordinate(9,1)));
            assertEquals(1,myBoard.numShipsSunk());

            assertTrue(myBoard.guessLocation(new Coordinate(9,9)));
            assertEquals(2,myBoard.numShipsSunk());
            assertFalse(myBoard.allShipsSunk());

            assertTrue(myBoard.guessLocation(new Coordinate(1,1)));
            assertTrue(myBoard.allShipsSunk());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testGuessingCoordsImproperly() {
        Board myBoard = new Board();

        assertThrows(IllegalArgumentException.class, () -> myBoard.guessLocation(new Coordinate(10, 5)));
        assertThrows(IllegalArgumentException.class, () -> myBoard.guessLocation(new Coordinate(-1, 1)));
        assertThrows(IllegalArgumentException.class, () -> myBoard.guessLocation(new Coordinate(8, 99)));
        assertThrows(IllegalArgumentException.class, () -> myBoard.guessLocation(new Coordinate(3, -2)));

        assertDoesNotThrow(() -> myBoard.guessLocation(new Coordinate(6, 6)));
        assertThrows(CoordinateAlreadyGuessedException.class, () -> myBoard.guessLocation(new Coordinate(6, 6)));
    }
}
