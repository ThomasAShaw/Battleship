import java.util.*;

public class Ship {
    private final ArrayList<Coordinate> occupiedCoordinates = new ArrayList<>();
    private final String name;
    private boolean isSunk = false;

    /**
     * Initialize a new Ship object.
     * @param name the name of the ship.
     * @param startCoordinate the starting Coordinate of the ship.
     * @param endCoordinate the ending Coordinate of the ship.
     */
    public Ship(String name, Coordinate startCoordinate, Coordinate endCoordinate) {
        /* Check coordinates are in line (NO DIAGONALS) */
        if (!isLinear(startCoordinate, endCoordinate)) {
            throw new IllegalArgumentException("Coordinates must line on same x-line or y-line; no diagonals!");
        }

        this.name = name;
        occupiedCoordinates.add(startCoordinate);

        int xDistance = startCoordinate.getX() - endCoordinate.getX();
        int yDistance = startCoordinate.getY() - endCoordinate.getY();
        int counter = 0;

        while (xDistance != 0 || yDistance != 0) {
            if (xDistance > 0) { /* East facing ship. */
                occupiedCoordinates.add(new Coordinate((startCoordinate.getX() + counter), startCoordinate.getY()));
                counter++;
                xDistance--;
            } else if (xDistance < 0) { /* West facing ship. */
                occupiedCoordinates.add(new Coordinate((startCoordinate.getX() - counter), startCoordinate.getY()));
                counter++;
                xDistance++;
            } else if (yDistance > 0) { /* North facing ship. */
                occupiedCoordinates.add(new Coordinate(startCoordinate.getX(), (startCoordinate.getY() + counter)));
                counter++;
                yDistance--;
            } else { /* South facing ship. */
                occupiedCoordinates.add(new Coordinate(startCoordinate.getX(), (startCoordinate.getY() + counter)));
                counter++;
                yDistance++;
            }
        }
    }

    /**
     * Return a list of all occupied Coordinates.
     * @return set containing all Coordinates occupied by the ship.
     */
    public List<Coordinate> getCoordinates() {
        return occupiedCoordinates;
    }

    /**
     * Return the Ship's name.
     * @return the name of the ship.
     */
    public String getName() {
        return name;
    }

    /**
     * Check if the Ship is sunk.
     * @return true if ship is sunk, false otherwise.
     */
    public boolean isSunk() {
        if (!isSunk) {
            for (Coordinate c : occupiedCoordinates) {
                if (!c.isGuessed()) {
                    return false;
                }
            }
            isSunk = true;
        }

        return true;
    }

    /**
     * Check if two points are in either a horizontal or vertical line.
     * @param a first coordinate.
     * @param b second coordinate.
     * @return true if points lie on same x or y line, false if diagonal or any other case.
     */
    private boolean isLinear(Coordinate a, Coordinate b) {
        if (a.getX() == b.getX() || a.getY() == b.getY()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Ship objShip = (Ship) obj;
        return Objects.equals(occupiedCoordinates, objShip.occupiedCoordinates) && Objects.equals(name, objShip.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(occupiedCoordinates, name);
    }
}