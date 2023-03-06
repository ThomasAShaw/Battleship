package battleship;

import java.util.Collections;
import java.util.List;

public class GameEvent {
    private static int totalEvents = 0;
    private int eventNum;
    private final GameEventType eventType;
    private final Coordinate coordinate;
    private final boolean isPlayerOneAttacker;
    private final List<String> extraInfo;

    /**
     * Initialises a new GameEvent object, representing an event that happened at one point during play.
     * @param eventType the type of event; not null.
     * @param coordinate the coordinate this event occurred; not null.
     * @param isPlayerOneAttacker true if player one was attacking, false for player two.
     * @param extraInfo any other info related to the event; not null, contains at least one element.
     */
    public GameEvent(GameEventType eventType, Coordinate coordinate, boolean isPlayerOneAttacker, List<String> extraInfo) {
        // TODO: Store what ship was at this event?
        this.eventNum = ++totalEvents;
        this.eventType = eventType;
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY(), coordinate.isGuessed(), null);
        this.isPlayerOneAttacker = isPlayerOneAttacker;
        this.extraInfo = Collections.unmodifiableList(extraInfo);
    }

    public static void resetCount() {
        // FIXME: this is quite messy.
        totalEvents = 0;
    }

    /**
     * @return attacker (active player) in this event; true for player one, false for player two.
     */
    public boolean getAttacker() {
        return isPlayerOneAttacker;
    }

    /**
     * @return victim (enemy player) in this event; true for player one, false for player two.
     */
    public boolean getVictim() {
        return !isPlayerOneAttacker;
    }

    /**
     * @return the type of event.
     */
    public GameEventType getEventType() {
        return eventType;
    }

    /**
     * @return the coordinate this event occurred at, does not include guess value and occupying ship.
     */
    public Coordinate getCoordinate() {
        return new Coordinate(coordinate.getX(), coordinate.getY());
    }

    /**
     * @return any extra info related to this game event.
     */
    public String getExtraInfo() {
        if (eventType == GameEventType.HIT) {
            return extraInfo.get(0); // Return if it was sink, or was a winning move.
        } else if (eventType == GameEventType.MISS) {
            return ""; // Nothing to return, just missed.
        } else if (eventType == GameEventType.FAIL) {
            return extraInfo.get(0); // Return the fail reason
        }

        return "";
    }

    public int getEventNum() {
        return eventNum;
    }

}
