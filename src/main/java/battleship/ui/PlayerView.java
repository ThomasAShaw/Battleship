package battleship.ui;

import battleship.Board;
import battleship.Game;
import battleship.InvalidPlacementException;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PlayerView {
    // TODO: placing ships, shooting ships, and waiting.
    // TODO: unplaced ship list.
    // Specific to one player, for one game.
    private final Game game;
    private final boolean isPlayerOne;
    public static final double GRID_CELL_SIZE = 50;
    private PlayerState currentState;
    private boolean isGameOver = false;
    private boolean areShipsSet = false;

    public PlayerView(Game game, boolean isPlayerOne) {
        this.game = game;
        this.isPlayerOne = isPlayerOne;
        currentState = PlayerState.WAITING;
    }

    public void getPlayerView() {
        if (!isGameOver) {
            switch (currentState) {
                case PREPARATION:
                    PreparationView prepView = new PreparationView(game, isPlayerOne);
                    currentState = PlayerState.WAITING;
                    areShipsSet = true; // TODO: implement this.
                    BattleshipApplication.activePane.setCenter(prepView.getPreparationView());
                    break;
                    // TODO: after this point, it should go to the other player.
                case WAITING:
                    if (areShipsSet) {
                        currentState = PlayerState.PLAYING;
                    } else {
                        currentState = PlayerState.PREPARATION;
                    }
                    BattleshipApplication.activePane.setCenter(getWaitingView());
                    break;
                case PLAYING:
                    break;
                case END:
                    break;
                }
            } else {
            // TODO: Game over screen...
        }
        System.out.println(currentState.toString());
    }

    private Parent getWaitingView() {
        BorderPane waitingLayout = new BorderPane();
        VBox waitingMenu = new VBox();
        waitingMenu.setAlignment(Pos.CENTER);

        Label waitingLabel = new Label("Ready to begin your turn Player " + (isPlayerOne ? "One" : "Two") + "?");
        Label privacyLabel = new Label("No peeking, Player " + (!isPlayerOne ? "One" : "Two" + "!"));
        Button beginTurnButton = new Button("Begin Turn");
        beginTurnButton.setOnAction((event) -> getPlayerView()); // TODO: Fix this.

        waitingMenu.getChildren().addAll(waitingLabel, privacyLabel, beginTurnButton);
        waitingLayout.setCenter(waitingMenu);
        return waitingLayout;
    }

}
