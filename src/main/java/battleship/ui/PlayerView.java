package battleship.ui;

import battleship.Game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PlayerView {
    // Specific to one player, for one game.
    private final Game game;
    private final boolean isPlayerOne;
    public static final double GRID_CELL_SIZE = 50;
    private PlayerState currentState = PlayerState.WAITING;
    private boolean isGameOver = false;
    private boolean areShipsSet = false;
    private final GameView gameView;
    private final BattleshipApplication app;

    public PlayerView(BattleshipApplication app, Game game, boolean isPlayerOne) {
        this.app = app;
        this.game = game;
        this.isPlayerOne = isPlayerOne;
        this.gameView = new GameView(app, game, isPlayerOne);
    }

    public Scene getPlayerView() {
        if (!isGameOver) {
            switch (currentState) {
                case PREPARATION:
                    PreparationView prepView = new PreparationView(app, game, isPlayerOne);
                    currentState = PlayerState.WAITING;
                    areShipsSet = true; // TODO: implement this.
                    return prepView.getPreparationView();
                    // TODO: after this point, it should go to the other player.
                case WAITING:
                    if (areShipsSet) {
                        currentState = PlayerState.PLAYING;
                    } else {
                        currentState = PlayerState.PREPARATION;
                    }
                    return getWaitingView();
                case PLAYING:
                    currentState = PlayerState.WAITING;
                    return gameView.getGameView();
                case END:
                    break;
                }
            } else {
            // TODO: Game over screen...
        }
        System.out.println(currentState.toString());
        return null; // TODO: Error screen.
    }

    private Scene getWaitingView() {
        VBox waitingMenu = new VBox();
        waitingMenu.setAlignment(Pos.CENTER);

        Label waitingLabel = new Label("Ready to begin your turn Player " + (isPlayerOne ? "One" : "Two") + "?");
        Label privacyLabel = new Label("No peeking, Player " + (!isPlayerOne ? "One" : "Two" + "!"));
        Button beginTurnButton = new Button("Begin Turn");
        beginTurnButton.setOnAction((event) -> app.switchScene(getPlayerView())); // TODO: Fix this.

        waitingMenu.getChildren().addAll(waitingLabel, privacyLabel, beginTurnButton);

        return new Scene(waitingMenu, 1000, 750);
    }
}
