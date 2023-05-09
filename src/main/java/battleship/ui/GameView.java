package battleship.ui;

import battleship.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameView {
    private final Game game;
    private final boolean isPlayerOne;
    private int lastUpdatedEventNum = 0;
    private final Map<Coordinate, Button> activePlayerButtons = new HashMap<>();
    private final Map<Coordinate, Button> enemyPlayerButtons = new HashMap<>();
    private GridPane activePlayerGrid = null;
    private GridPane enemyPlayerGrid = null;
    private boolean takeInput; // for disabling buttons, potentially change this.
    public static final double GRID_CELL_SIZE = PlayerView.GRID_CELL_SIZE;
    private final BattleshipApplication app;

    public GameView(BattleshipApplication app, Game game, boolean isPlayerOne) {
        this.app = app;
        this.game = game;
        this.isPlayerOne = isPlayerOne;
    }

    public Scene getGameView() {
        if (activePlayerGrid == null || enemyPlayerGrid == null) {
            activePlayerGrid = getPlayerGrid(true);
            enemyPlayerGrid = getPlayerGrid(false);
        }
        updatePlayerGrid();
        takeInput = true;

        HBox grids = new HBox();
        grids.getChildren().addAll(activePlayerGrid, enemyPlayerGrid);
        grids.setPadding(new Insets(20));
        grids.setSpacing(20);

        Label yourGridLabel = new Label("Your Grid");
        yourGridLabel.getStyleClass().add("grid-title");

        Label enemyGridLabel = new Label("Enemy Grid");
        enemyGridLabel.getStyleClass().add("grid-title");

        // Add labels to the scene
        VBox playerOneGrid = new VBox(yourGridLabel, activePlayerGrid);
        VBox playerTwoGrid = new VBox(enemyGridLabel, enemyPlayerGrid);

        grids.getChildren().addAll(playerOneGrid, playerTwoGrid);

        Label instructionLabel = new Label("Click a grid square on the enemy grid to attack.\nYou may play until you miss.");
        instructionLabel.getStyleClass().add("instructions");

        VBox root = new VBox(grids, instructionLabel);
        root.getStyleClass().add("watery-background");
        root.getStyleClass().add("side-menu");
        Scene gameViewScene = new Scene(root, 1150, 850);
        gameViewScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/battleship.css")).toExternalForm());
        return gameViewScene;
    }

    private GridPane getPlayerGrid (boolean isCurrentPlayer) {
        boolean getPlayerOne = isCurrentPlayer == isPlayerOne;
        GridPane playerGrid = new GridPane();

        /* Create player board grid, borrowed from PreparationView. */
        for (int i = 0; i < Board.DEFAULT_SIZE; i++) {
            playerGrid.getColumnConstraints().add(new ColumnConstraints(GRID_CELL_SIZE));
            playerGrid.getRowConstraints().add(new RowConstraints(50));
        }

        /* Set letter and number labels on side of player grid. */
        for (int i = 0; i < game.getBoardWidth(getPlayerOne) + 1; i++) {
            Label letterLabel = new Label(i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : "*");
            Label numberLabel = new Label(i > 0 ? Integer.toString(i) : "*");
            letterLabel.getStyleClass().add("grid-label");
            numberLabel.getStyleClass().add("grid-label");

            letterLabel.setPrefWidth(GRID_CELL_SIZE);
            letterLabel.setMinWidth(GRID_CELL_SIZE);
            letterLabel.setPrefHeight(GRID_CELL_SIZE);
            letterLabel.setAlignment(Pos.CENTER);

            numberLabel.setPrefWidth(GRID_CELL_SIZE);
            numberLabel.setPrefHeight(GRID_CELL_SIZE);
            numberLabel.setAlignment(Pos.CENTER);

            if (i != 0) {
                playerGrid.add(letterLabel, i, 0);
                playerGrid.add(numberLabel, 0, i);
            } else {
                playerGrid.add(letterLabel, i, i);
            }
        }

        /* Add buttons to player grid. */
        for (int y = 0; y < game.getBoardHeight(getPlayerOne); y++) {
            for (int x = 0; x < game.getBoardWidth(getPlayerOne); x++) {

                Button button = new Button();
                button.setPrefSize(GRID_CELL_SIZE, GRID_CELL_SIZE);
                button.getStyleClass().add("grid-button");
                /* Add one as letter and number cells take up first row and column, respectively. */
                playerGrid.add(button, x + 1, y + 1);

                if (isCurrentPlayer) {
                    activePlayerButtons.put(new Coordinate(x + 1, y + 1), button);
                } else {
                    enemyPlayerButtons.put(new Coordinate(x + 1, y + 1), button);
                    // On action, should count as a guess...
                    button.setOnAction((event) -> {
                        if (takeInput)
                            handleGuess(button);});
                }
            }
        }

        // Make sure all active player ships show up on their board...
        int shipNum = 1;
        for (Ship s : game.getPlacedShips(isPlayerOne)) {
            for (Coordinate c : s.getCoordinates()) {
                Button shipButton = activePlayerButtons.get(new Coordinate(c.getX() + 1, c.getY() + 1));
                shipButton.setText("S" + shipNum);
                shipButton.getStyleClass().add("placed-ship");
            }
            shipNum++;
        }

        return playerGrid;
    }

    private void updatePlayerGrid() {
        List<GameEvent> newEvents = game.getNewEvents(lastUpdatedEventNum);
        if (newEvents.size() > 0) {
            lastUpdatedEventNum = newEvents.get(newEvents.size() - 1).getEventNum();

            for (GameEvent event : newEvents) {
                Coordinate coordBoard = event.getCoordinate();
                Coordinate coordGrid = new Coordinate(coordBoard.getX() + 1, coordBoard.getY() + 1); // add one from labels on side.
                GameEventType eventType = event.getEventType();
                boolean isAttackingPlayerOne = event.getAttacker();

                // Determine which board this goes on...
                // If attacking player matches player one, then active player was the attacker...
                Button impactedButton;
                if (isAttackingPlayerOne == isPlayerOne) {
                    impactedButton = enemyPlayerButtons.get(coordGrid);
                } else { // else, active player is victim...
                    impactedButton = activePlayerButtons.get(coordGrid);
                }

                if (eventType == GameEventType.HIT) {
                    if (event.getExtraInfo().contains("sunk")) {
                        List<Coordinate> sunkCoords = game.getAssociatedShipCoords(event.getVictim(), coordBoard.getX(), coordBoard.getY());
                        System.out.println("SUNK!!");

                        for (Coordinate coord : sunkCoords) {
                            System.out.println(coord);
                            Button sunkButton;
                            Coordinate coordOnGrid = new Coordinate(coord.getX() + 1, coord.getY() + 1);

                            if (isAttackingPlayerOne == isPlayerOne) {
                                sunkButton = enemyPlayerButtons.get(coordOnGrid);
                            } else { // else, active player is victim...
                                sunkButton = activePlayerButtons.get(coordOnGrid);
                            }

                            sunkButton.getStyleClass().add("sunk-ship-cell");
                            sunkButton.setText("\uD83D\uDC80");
                        }
                    } else {
                        impactedButton.getStyleClass().add("hit-cell");
                        impactedButton.setText("✖");
                    }
                } else if (eventType == GameEventType.MISS) {
                    impactedButton.getStyleClass().add("miss-cell");
                    impactedButton.setText("\uD83C\uDF0A");
                }
            }
        }
    }

    private boolean handleGuess(Button guessedButton) {
        // Guess at that location...
        int xCoord = GridPane.getColumnIndex(guessedButton) - 1; // As left number column is at position 0.
        int yCoord = GridPane.getRowIndex(guessedButton) - 1; // As top letter row is at position 0.
        String guessStatus = game.guessLocation(isPlayerOne, new Coordinate(xCoord, yCoord));

        if (guessStatus.contains("HIT")) {
            // Hit, so keep going unless win...
            updatePlayerGrid();
            if (guessStatus.contains("won")) {
                handleEndGame();
            }
            return true;
        } else if (guessStatus.contains("MISS")) {
            // Missed, so switch player turn...
            // TODO: Add in miss label
            updatePlayerGrid();
            takeInput = false;

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> app.switchPlayer());
            pause.play();

            return true;
        } else {
            // Failed, so display error code.
            // TODO: Doesn't do anything right now.
            System.out.println(guessStatus);
            return false;
        }
    }

    // TODO: Implement this.
    private void handleEndGame() {
        game.endGame();
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> app.gameOver());
        pause.play();
    }
}
