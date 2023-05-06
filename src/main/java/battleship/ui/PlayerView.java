package battleship.ui;

import battleship.Game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.Objects;
import java.util.Random;

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
            }
        System.out.println(currentState.toString());
        return null; // TODO: Error screen.
    }

    private Scene getWaitingView() {
        VBox waitingMenu = new VBox(30);
        waitingMenu.setAlignment(Pos.CENTER);
        waitingMenu.getStyleClass().add("waiting-menu");

        Label waitingLabel = new Label("Ready to begin your turn\nPlayer " + (isPlayerOne ? "One" : "Two") + "?");
        waitingLabel.getStyleClass().add("waiting-label");

        Label privacyLabel = new Label("No peeking, Player " + (!isPlayerOne ? "One" : "Two") + "!");
        privacyLabel.getStyleClass().add("privacy-label");

        Button beginTurnButton = new Button("Begin Turn");
        beginTurnButton.getStyleClass().add("begin-turn-button");
        beginTurnButton.setOnAction((event) -> app.switchScene(getPlayerView()));

        Button killTimeButton = new Button("Kill Time");
        killTimeButton.getStyleClass().add("menu-button");
        killTimeButton.setOnAction((event) -> waitingMenu.setStyle("-fx-background-color: " + generateRandomColor()));

        waitingMenu.getChildren().addAll(waitingLabel, privacyLabel, beginTurnButton, killTimeButton);

        Scene waitingScene = new Scene(waitingMenu, 1000, 750);
        waitingScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/battleship.css")).toExternalForm());

        return waitingScene;
    }

    private String generateRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        int redSecond = red + random.nextInt(30, 80);
        redSecond = redSecond > 256 ? redSecond - 256 : redSecond;
        int greenSecond = green + random.nextInt(30, 80);
        greenSecond = greenSecond > 256 ? greenSecond - 256 : greenSecond;
        int blueSecond = blue + random.nextInt(30, 80);
        blueSecond = blueSecond > 256 ? blueSecond - 256 : blueSecond;

        return String.format("linear-gradient(to bottom, rgba(%d, %d, %d, 1), rgba(%d, %d, %d, 1))", red, green, blue, redSecond, greenSecond, blueSecond);

    }
}
