import java.util.Objects;

public class Coordinate {
    private final int x;
    private final int y;

    /**
     * Initialises a new Coordinate object.
     * @param x the horizontal position.
     * @param y the vertical position.
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Provides the coordinate's horizontal x-value.
     * @return the coordinate's x-value.
     */
    public int getX() {
        return x;
    }

    /**
     * Provides the coordinate's vertical y-value.
     * @return the coordinate's y-value.
     */
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Coordinate objCoordinate = (Coordinate) obj;
        return (this.x == objCoordinate.x && this.y == objCoordinate.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
