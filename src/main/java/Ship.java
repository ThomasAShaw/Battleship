import java.util.*;

public class Ship {
    private final Map<Coordinate, Boolean> occupiedCoordinates = new HashMap<>();
    private final String name;

    /**
     * Initialize a new Ship object.
     * @param name the name of the ship.
     * @param startCoordinate the starting Coordinate of the ship.
     * @param endCoordinate the ending Coordinate of the ship.
     */
    public Ship(String name, Coordinate startCoordinate, Coordinate endCoordinate) {
        // Check coordinates are in line (NO DIAGONALS)
        if (!isLinear(startCoordinate, endCoordinate)) {
            throw new IllegalArgumentException("Coordinates must line on same x-line or y-line. No diagonals either!");
        }

        this.name = name;
        occupiedCoordinates.put(startCoordinate, false);

        int xDistance = startCoordinate.getX() - endCoordinate.getX();
        int yDistance = startCoordinate.getY() - endCoordinate.getY();
        int counter = 0;

        while (xDistance != 0 || yDistance != 0) {
            if (xDistance > 0) {
                occupiedCoordinates.put(new Coordinate((startCoordinate.getX() + counter), startCoordinate.getY()), false);
                counter++;
                xDistance--;
            } else if (xDistance < 0) {
                occupiedCoordinates.put(new Coordinate((startCoordinate.getX() - counter), startCoordinate.getY()), false);
                counter++;
                xDistance++;
            } else if (yDistance > 0) {
                occupiedCoordinates.put(new Coordinate(startCoordinate.getX(), (startCoordinate.getY() + counter)), false);
                counter++;
                yDistance--;
            } else {
                occupiedCoordinates.put(new Coordinate(startCoordinate.getX(), (startCoordinate.getY() + counter)), false);
                counter++;
                yDistance++;
            }
        }
    }

    /**
     * Return a set of all occupied coordinates.
     * @return set containing all coordinates occupied by the ship.
     */
    public Set<Coordinate> getCoordinates() {
        return occupiedCoordinates.keySet();
    }

    /**
     * Return the ship's name.
     * @return the name of the ship.
     */
    public String getName() {
        return name;
    }

    /**
     * Attempt to hit one of the ship's coordinates.
     * @param guessCoordinate coordinate to hit.
     * @return true if guessed coordinate successfully hit the ship;
     * false if not a ship coordinate or was already hit at this coordinate.
     */
    public boolean hitShip(Coordinate guessCoordinate) {
        Boolean hitStatus = occupiedCoordinates.containsKey(guessCoordinate);
        if (hitStatus == null || hitStatus) {
            return false;
        }

        occupiedCoordinates.put(guessCoordinate, true);
        return true;
    }

    /**
     * Check if the ship is sunk.
     * @return true if ship is sunk, false otherwise.
     */
    public boolean isSunk() {
        if (occupiedCoordinates.containsValue(false)) {
            return false;
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
        return Objects.equals(occupiedCoordinates.keySet(), objShip.occupiedCoordinates.keySet()) && Objects.equals(name, objShip.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(occupiedCoordinates.keySet(), name);
    }
}
