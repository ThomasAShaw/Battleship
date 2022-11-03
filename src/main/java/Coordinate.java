import java.util.Objects;

public class Coordinate {
    private final int x;
    private final int y;
    private boolean hit;

    /**
     * Initialises a new Coordinate object.
     * @param x the horizontal position.
     * @param y the vertical position.
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
        this.hit = false;
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

    /**
     * Provides the coordinate's hit-value.
     * @return whether the coordinate has been hit or not.
     */
    public boolean isHit() {
        return hit;
    }

    /**
     * Modifies the hit-value.
     * @param isHit the hit-value to set coordinate to.
     */
    public void setHit(boolean isHit) {
        this.hit = isHit;
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
