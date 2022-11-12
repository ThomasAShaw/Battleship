package battleship;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTests {

    @Test
    public void testConstructorsNulls() {
        assertThrows(IllegalArgumentException.class, () -> new Ship("My Ship", null, null));
        assertThrows(IllegalArgumentException.class, () -> new Ship("My Ship", null));
    }

    @Test
    public void testConstructorStartEndInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            new Ship("Broken Ship", new Coordinate(0, 5), new Coordinate(1, 6)));
        assertThrows(IllegalArgumentException.class, () ->
            new Ship("Broken Ship", new Coordinate(3, 10), new Coordinate(10, 3)));
        assertThrows(IllegalArgumentException.class, () ->
            new Ship("Broken Ship", new Coordinate(9, 4), new Coordinate(8, 10)));
    }

    /* Size limit is Ship.SIZE_LIMIT */
    @Test
    public void testConstructorStartEndLimit() {
        assertThrows(IllegalArgumentException.class, () ->
                new Ship("The BIG Ship", new Coordinate(Integer.MAX_VALUE, 7), new Coordinate(10, 7)));
        assertThrows(IllegalArgumentException.class, () ->
                new Ship("The KINDA big ship", new Coordinate(1129, 0), new Coordinate(1129, Ship.SIZE_LIMIT)));
        assertThrows(IllegalArgumentException.class, () ->
                new Ship("The other KINDA big ship", new Coordinate(12, 9), new Coordinate(12 - Ship.SIZE_LIMIT, 9)));
        assertDoesNotThrow(() -> new Ship("The legally sized ship", new Coordinate(Integer.MIN_VALUE, Ship.SIZE_LIMIT - 1), new Coordinate(Integer.MIN_VALUE, 0)));
        assertDoesNotThrow(() -> new Ship("Another legally sized ship", new Coordinate(Integer.MAX_VALUE, Integer.MAX_VALUE), new Coordinate(Integer.MAX_VALUE - Ship.SIZE_LIMIT + 1, Integer.MAX_VALUE)));
    }

    @Test
    public void testConstructorDefaults() {
        List<Coordinate> coordsList = List.of(
            new Coordinate(0, 1),
            new Coordinate(0, 2),
            new Coordinate(0, 3),
            new Coordinate(0, 4));
        Ship myShipA = new Ship("Ship McShipface", coordsList.get(0), coordsList.get(3));
        Ship myShipB = new Ship("Ship McShipface", coordsList);

        assertEquals("Ship McShipface", myShipA.getName());
        assertEquals("Ship McShipface", myShipB.getName());

        assertEquals(coordsList, myShipA.getCoordinates());
        assertEquals(coordsList, myShipB.getCoordinates());

        assertFalse(myShipA.isSunk());
        assertFalse(myShipB.isSunk());

        assertEquals(myShipA, myShipB);
        assertEquals(myShipB, myShipA);
    }

    @Test
    public void testConstructorCoordsNorth() {
        List<Coordinate> coordsList = List.of(
                new Coordinate(973, 8),
                new Coordinate(973, 7),
                new Coordinate(973, 6),
                new Coordinate(973, 5),
                new Coordinate(973, 4),
                new Coordinate(973, 3)
        );

        Ship myShipA = new Ship("Mega yacht", coordsList.get(0), coordsList.get(coordsList.size() - 1));
        Ship myShipB = new Ship("Mega yacht", coordsList);

        assertEquals(coordsList, myShipA.getCoordinates());
        assertEquals(coordsList, myShipB.getCoordinates());
        assertEquals(myShipA, myShipB);
    }

    @Test
    public void testConstructorCoordsSouth() {
        List<Coordinate> coordsList = List.of(
                new Coordinate(Integer.MIN_VALUE, -1),
                new Coordinate(Integer.MIN_VALUE, 0),
                new Coordinate(Integer.MIN_VALUE, 1),
                new Coordinate(Integer.MIN_VALUE, 2)
        );

        Ship myShipA = new Ship("Speedboat!!!!", coordsList.get(0), coordsList.get(coordsList.size() - 1));
        Ship myShipB = new Ship("Speedboat", coordsList);

        assertEquals(coordsList, myShipA.getCoordinates());
        assertEquals(coordsList, myShipB.getCoordinates());
        assertNotEquals(myShipA, myShipB);
    }

    @Test
    public void testConstructorCoordsEast() {
        List<Coordinate> coordsList = List.of(
                new Coordinate(Integer.MAX_VALUE - 4, 1888),
                new Coordinate(Integer.MAX_VALUE - 3, 1888),
                new Coordinate(Integer.MAX_VALUE - 2, 1888),
                new Coordinate(Integer.MAX_VALUE - 1, 1888),
                new Coordinate(Integer.MAX_VALUE, 1888)
        );

        Ship myShipA = new Ship("fIsHiNg bOat", coordsList.get(0), coordsList.get(coordsList.size() - 1));
        Ship myShipB = new Ship("fishing boat", coordsList);

        assertEquals(coordsList, myShipA.getCoordinates());
        assertEquals(coordsList, myShipB.getCoordinates());
        assertNotEquals(myShipA, myShipB);
    }

    @Test
    public void testConstructorCoordsWest() {
        List<Coordinate> coordsList = List.of(
                new Coordinate(3, -12),
                new Coordinate(2, -12),
                new Coordinate(1, -12),
                new Coordinate(0, -12),
                new Coordinate(-1, -12),
                new Coordinate(-2, -12),
                new Coordinate(-3, -12),
                new Coordinate(-4, -12)
        );

        Ship myShipA = new Ship("totally not suspicious boat", coordsList.get(0), coordsList.get(coordsList.size() - 1));
        Ship myShipB = new Ship("totally not suspicious boat", coordsList);

        assertEquals(coordsList, myShipA.getCoordinates());
        assertEquals(coordsList, myShipB.getCoordinates());
        assertEquals(myShipA, myShipB);
    }

    @Test
    public void testConstructorSingleCoord() {
        Coordinate coord = new Coordinate(1, 2);

        Ship myShipA = new Ship("itsy-bitsy-teeny-weeny-yellow-polka-dot boat", coord, coord);
        Ship myShipB = new Ship("itsy-bitsy-teeny-weeny-yellow-polka-dot boat", List.of(coord));

        assertEquals(List.of(coord), myShipA.getCoordinates());
        assertEquals(List.of(coord), myShipB.getCoordinates());
        assertEquals(myShipA, myShipB);
    }

    @Test
    public void testShipCoordSetting() {
        List<Coordinate> coordsList = List.of(
                new Coordinate(-100, 12644),
                new Coordinate(-100, 12645),
                new Coordinate(-100, 12646)
        );
        Ship myShip = new Ship("Testing Ship", new Coordinate(-5, 0), new Coordinate(-4, 0));
        List<Coordinate> shipCoords = myShip.getCoordinates();

        for(Coordinate c : shipCoords) {
            assertEquals(myShip, c.getOccupyingShip());
        }
    }

    @Test
    public void testShipMutabilityConstructorCoords() {
        List<Coordinate> coordsList = List.of(
                new Coordinate(0, 1),
                new Coordinate(0, 2),
                new Coordinate(0, 3),
                new Coordinate(0, 4),
                new Coordinate(0, 5)
        );
        Ship myShipA = new Ship("Ship", coordsList.get(0), coordsList.get(coordsList.size() - 1));
        Ship myShipB = new Ship("Ship", coordsList);
        assertEquals(myShipA, myShipB);

        for (Coordinate c : coordsList) {
            try {
                c.guessCoordinate();
            } catch (Exception e) {
                fail();
            }
        }

        assertFalse(myShipA.isSunk());
        assertFalse(myShipB.isSunk());

        List<Coordinate> allShipCoords = myShipA.getCoordinates();
        allShipCoords.addAll(myShipB.getCoordinates());

        for (Coordinate c : allShipCoords) {
            assertFalse(c.isGuessed());
            assertEquals(myShipA, c.getOccupyingShip());
        }
    }

    @Test
    public void testShipMutabilityGetCoords() {
        List<Coordinate> coordsList = List.of(
                new Coordinate(0, 1),
                new Coordinate(0, 2),
                new Coordinate(0, 3),
                new Coordinate(0, 4),
                new Coordinate(0, 5)
        );
        Ship myShipMutated = new Ship("Ship", coordsList.get(0), coordsList.get(coordsList.size() - 1));
        Ship myShipUnmutated = new Ship("Ship", coordsList);
        assertEquals(myShipMutated, myShipUnmutated);

        List<Coordinate> allShipCoords = myShipMutated.getCoordinates();

        for (Coordinate c : allShipCoords) {
            try {
                c.guessCoordinate();
            } catch (Exception e) {
                fail();
            }
        }

        assertTrue(myShipMutated.isSunk());
        assertFalse(myShipUnmutated.isSunk());
    }
}
