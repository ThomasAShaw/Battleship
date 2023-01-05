package battleship.ui;

import battleship.*;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameView {
    private final Game game;
    private final boolean isPlayerOne;
    private BorderPane gameLayout;
    private int lastUpdatedEventNum = 0;
    private final Map<Coordinate, Button> activePlayerButtons = new HashMap<>();
    private final Map<Coordinate, Button> enemyPlayerButtons = new HashMap<>();
    private GridPane activePlayerGrid;
    private GridPane enemyPlayerGrid;
    public static final double GRID_CELL_SIZE = PlayerView.GRID_CELL_SIZE;

    public GameView(Game game, boolean isPlayerOne) {
        this.game = game;
        this.isPlayerOne = isPlayerOne;
    }

    public Parent getGameView() {
        gameLayout = new BorderPane();
        activePlayerGrid = getPlayerGrid(true);
        enemyPlayerGrid = getPlayerGrid(false);

        HBox grids = new HBox();
        grids.getChildren().addAll(activePlayerGrid, enemyPlayerGrid);
        gameLayout.setCenter(grids);

        return gameLayout;
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
            Label letterLabel = new Label(i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : "");
            Label numberLabel = new Label(i > 0 ? Integer.toString(i) : "");

            letterLabel.setPrefWidth(GRID_CELL_SIZE);
            letterLabel.setPrefHeight(GRID_CELL_SIZE);
            letterLabel.setAlignment(Pos.CENTER);

            numberLabel.setPrefWidth(GRID_CELL_SIZE);
            numberLabel.setPrefHeight(GRID_CELL_SIZE);
            numberLabel.setAlignment(Pos.CENTER);

            playerGrid.add(letterLabel, i, 0);
            playerGrid.add(numberLabel, 0, i);
        }

        /* Add buttons to player grid. */
        for (int y = 0; y < game.getBoardHeight(getPlayerOne); y++) {
            for (int x = 0; x < game.getBoardWidth(getPlayerOne); x++) {

                Button button = new Button();
                button.setPrefSize(GRID_CELL_SIZE, GRID_CELL_SIZE);
                button.setStyle("-fx-background-radius: 0");
                /* Add one as letter and number cells take up first row and column, respectively. */
                playerGrid.add(button, x + 1, y + 1);

                if (isCurrentPlayer) {
                    activePlayerButtons.put(new Coordinate(x + 1, y + 1), button);
                } else {
                    enemyPlayerButtons.put(new Coordinate(x + 1, y + 1), button);
                    // On action, should count as a guess...
                    int finalX = x;
                    int finalY = y;
                    button.setOnAction((event) -> {
                        game.guessLocation(isPlayerOne, new Coordinate(finalX, finalY));
                        updatePlayerGrid(activePlayerGrid, enemyPlayerGrid);
                        System.out.println(finalX + " " + finalY);
                    });
                }
            }
        }

        return playerGrid;
    }

    private void updatePlayerGrid(GridPane activePlayerGrid, GridPane enemyPlayerGrid) {
        List<GameEvent> newEvents = game.getNewEvents(lastUpdatedEventNum);
        if (newEvents.size() > 0) {
            lastUpdatedEventNum = newEvents.get(newEvents.size() - 1).getEventNum();
            // TODO: double check this...
            boolean activePlayer = isPlayerOne;
            boolean enemyPlayer = !isPlayerOne;

            for (GameEvent event : newEvents) {
                Coordinate coordBoard = event.getCoordinate();
                Coordinate coordGrid = new Coordinate(coordBoard.getX() + 1, coordBoard.getY() + 1); // add one from labels on side.
                GameEventType eventType = event.getEventType();
                boolean isAttackingPlayerOne = event.getAttacker();

                // Determine which board this goes on...
                // If attacking player matches player one, then active player was the attacker...
                if (isAttackingPlayerOne == isPlayerOne) {
                    Button impactedButton = enemyPlayerButtons.get(coordGrid);
                    if (eventType == GameEventType.HIT) {
                        impactedButton.setText("X");
                    } else if (eventType == GameEventType.MISS) {
                        impactedButton.setText("O");
                    }
                } else { // else, active player is victim...
                    Button impactedButton = activePlayerButtons.get(coordGrid);
                    if (eventType == GameEventType.HIT) {
                        impactedButton.setText("X");
                    } else if (eventType == GameEventType.MISS) {
                        impactedButton.setText("X");
                    }
                }
            }
        }
    }
}
