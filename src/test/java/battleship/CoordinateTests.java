package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoordinateTests {

    @Test
    public void testDefaults() {
        Coordinate myCoord = new Coordinate(1, 2);

        assertEquals(1, myCoord.getX());
        assertEquals(2, myCoord.getY());
        assertFalse(myCoord.isGuessed());
        assertFalse(myCoord.isOccupied());
    }

    @Test void testAddingShip() {
        Coordinate coordA = new Coordinate(0, 6);
        Coordinate coordB = new Coordinate(0, 9);
        Ship testShip = new Ship("Bertha", coordA, coordB);

        assertTrue(coordB.setShip(testShip));
        assertFalse(coordA.isOccupied());
        assertTrue(coordB.isOccupied());
    }

    @Test
    void testAddingDuplicateShip() {
        Coordinate testCoord = new Coordinate(0, 7);
        Ship testShipA = new Ship("My Cool Ship", testCoord, testCoord);
        Ship testShipB = new Ship("My Cooler Ship", testCoord, new Coordinate(7, 7));

        assertTrue(testCoord.setShip(testShipA));
        assertFalse(testCoord.setShip(testShipB));
        assertTrue(testCoord.isOccupied());
        assertEquals(testShipA, testCoord.getOccupyingShip());
    }

    @Test
    public void testEquality() {
        Coordinate coordA = new Coordinate(5, 10);
        Coordinate coordB = new Coordinate(5, 10);
        Coordinate coordC = new Coordinate(5, 10);

        coordB.setShip(new Ship("Ol' Betsy", coordA, coordB));

        try {
            assertFalse(coordC.guessCoordinate());
            assertEquals(coordA, coordB);
            assertEquals(coordA, coordC);
            assertEquals(coordB, coordA);
            assertEquals(coordB, coordC);
            assertEquals(coordC, coordA);
            assertEquals(coordC, coordB);
        } catch (CoordinateAlreadyGuessedException e) {
            fail();
        }
    }

    @Test
    public void testGuessingCoordinates() {
        Coordinate coordA = new Coordinate(-5, -1);
        Coordinate coordB = new Coordinate(-1, -99);
        Coordinate coordC = new Coordinate(coordB.getX(), coordA.getY());

        assertFalse(coordA.isGuessed());
        assertFalse(coordB.isGuessed());
        assertFalse(coordC.isGuessed());

        coordA.setShip(new Ship("Insert Good Ship Name Here", coordC, coordA));

        try {
            assertTrue(coordA.guessCoordinate());
            assertFalse(coordB.guessCoordinate());
            assertFalse(coordC.guessCoordinate());
            // Subsequent guesses should throw an exception.
            assertThrows(CoordinateAlreadyGuessedException.class, coordA::guessCoordinate);
        } catch (CoordinateAlreadyGuessedException e) {
            fail();
        }
    }
}
