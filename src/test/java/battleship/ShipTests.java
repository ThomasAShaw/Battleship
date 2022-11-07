package battleship;

import org.junit.jupiter.api.Test;
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
}
