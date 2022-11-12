package battleship;

import java.util.*;

public class Ship {
    private final ArrayList<Coordinate> occupiedCoordinates = new ArrayList<>();
    private final String name;
    private boolean isSunk = false;
    static final int SIZE_LIMIT = 10;

    /**
     * Initialize a new ship object.
     * @param name the name of the ship.
     * @param startCoordinate the starting coordinate of the ship; not null.
     * @param endCoordinate the ending coordinate of the ship; not null.
     */
    public Ship(String name, Coordinate startCoordinate, Coordinate endCoordinate) {
        if (startCoordinate == null || endCoordinate == null) {
            throw new IllegalArgumentException("Coordinate(s) cannot be null.");
        }

        /* Check coordinates are in line (NO DIAGONALS) */
        if (!isLinear(startCoordinate, endCoordinate)) {
            throw new IllegalArgumentException("Coordinates must line on same x-line or y-line; no diagonals!");
        }

        this.name = name;
        occupiedCoordinates.add(new Coordinate(startCoordinate.getX(), startCoordinate.getY()));

        int xDistance = endCoordinate.getX() - startCoordinate.getX();
        int yDistance = endCoordinate.getY() - startCoordinate.getY();
        int counter = 1; // Add one as we add the starting coordinate beforehand.

        /* Check distance isn't greater than limit (add one to count starting coordinate). */
        if (Math.abs(xDistance) + 1 > SIZE_LIMIT || Math.abs(yDistance) + 1 > SIZE_LIMIT) {
            throw new IllegalArgumentException("Ship spans coordinates greater than size limit.");
        }

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
                occupiedCoordinates.add(new Coordinate(startCoordinate.getX(), (startCoordinate.getY() - counter)));
                counter++;
                yDistance++;
            }
        }

        setCoordsOccupied();
    }

    /**
     * Initialize a new ship object.
     * @param name the name of the ship.
     * @param coords all the occupying coordinates of the ship; coordinates must be in a line, with no diagonals; not null.
     */
    public Ship(String name, List<Coordinate> coords) {
        this.name = name;
        /* Ensure coordinates are in proper form. */
        if (coords == null) {
            throw new IllegalArgumentException("Coordinate(s) cannot be null.");
        }

        if (coords.size() == 0) {
            throw new IllegalArgumentException("Ship must occupy at least one coordinate");
        }

        occupiedCoordinates.add(new Coordinate(coords.get(0).getX(), coords.get(0).getY()));
        for (int i = 0; i < coords.size() - 1; i++) {
            if (isLinear(coords.get(i), coords.get(i + 1))) {
                occupiedCoordinates.add(new Coordinate(coords.get(i + 1).getX(), coords.get(i + 1).getY()));
            } else {
                throw new IllegalArgumentException("Coordinates must be in linear order, with no diagonals.");
            }
        }

        setCoordsOccupied();
    }

    /**
     * Return a list of all occupied Coordinates.
     * @return list containing all Coordinates occupied by the ship.
     * NOTE: coordinates mutated in list will mutate this ship's coordinates; this is deliberate.
     */
    public List<Coordinate> getCoordinates() {
        return new ArrayList<>(occupiedCoordinates);
    }

    /**
     * Return the ship's name.
     * @return the name of the ship.
     */
    public String getName() {
        return name;
    }

    /**
     * Check if the ship is sunk.
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

    /**
     * Set all coordinates of the ship as occupied by this ship.
     * Used by constructors when setting up occupied coordinates.
     */
    private void setCoordsOccupied() {
        for (Coordinate c : occupiedCoordinates) {
            c.setShip(this);
        }
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
