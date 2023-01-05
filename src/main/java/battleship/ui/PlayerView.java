package battleship.ui;

import battleship.Board;
import battleship.Game;
import battleship.InvalidPlacementException;

import javafx.scene.Parent;

public class PlayerView {
    // TODO: placing ships, shooting ships, and waiting.
    // TODO: unplaced ship list.
    // Specific to one player, for one game.
    private final Game game;
    private final boolean isPlayerOne;
    public static final double GRID_CELL_SIZE = 50;

    public PlayerView(Game game, boolean isPlayerOne) {
        this.game = game;
        this.isPlayerOne = isPlayerOne;
    }

    public Parent getPlayerView() throws InvalidPlacementException {
        // TODO: Implement this, based on game stage.
        PreparationView pv = new PreparationView(game, isPlayerOne);
        return pv.getPreparationView();
    }

}
