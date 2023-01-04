package battleship.ui;

import battleship.Board;
import battleship.Coordinate;
import battleship.InvalidPlacementException;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;

public class PlayerView {
    // TODO: placing ships, shooting ships, and waiting.
    // TODO: unplaced ship list.
    private final Board playerBoard;
    public static final double GRID_CELL_SIZE = 50;
    private int shipsPlaced = 0;
    private boolean[][] occupiedCoords;

    public PlayerView(Board playerBoard) {
        this.playerBoard = playerBoard;
        occupiedCoords = new boolean[playerBoard.getYSize()][playerBoard.getXSize()];
    }

    public Parent getPlayerView() throws InvalidPlacementException {
        // TODO: Implement this.
        return null;
    }

    /**
     * Get the window for when a player is setting their ships' positions.
     *
     * @param isPlayerOne true if player one is playing currently, false for player two.
     * @return the window of the preparation view.
     */
    public Parent getPreparationView(boolean isPlayerOne) {

        BorderPane preparationLayout = new BorderPane();
        GridPane activePlayerGrid = new GridPane();
        VBox shipPlacingBox = new VBox();

        // Create active player board grid
        for (int i = 0; i < Board.DEFAULT_SIZE; i++) {
            activePlayerGrid.getColumnConstraints().add(new ColumnConstraints(50));
            activePlayerGrid.getRowConstraints().add(new RowConstraints(50)); // TODO: may want to modify this.
        }

        // Set letter and number labels
        for (int i = 0; i < playerBoard.getXSize() + 1; i++) {
            Label letterLabel = new Label(i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : "");
            Label numberLabel = new Label(i > 0 ? Integer.toString(i) : "");

            letterLabel.setPrefWidth(GRID_CELL_SIZE);
            letterLabel.setPrefHeight(GRID_CELL_SIZE);
            letterLabel.setAlignment(Pos.CENTER);

            numberLabel.setPrefWidth(GRID_CELL_SIZE);
            numberLabel.setPrefHeight(GRID_CELL_SIZE);
            numberLabel.setAlignment(Pos.CENTER);

            activePlayerGrid.add(letterLabel, i, 0);
            activePlayerGrid.add(numberLabel, 0, i);
        }

        // Add buttons to player board grid
        for (int y = 0; y < playerBoard.getYSize(); y++) {
            for (int x = 0; x < playerBoard.getXSize(); x++) {
                // For dragging functionality
                StackPane gridCell = new StackPane();
                gridCell.setPrefSize(GRID_CELL_SIZE, GRID_CELL_SIZE);
                setOnDragOver(gridCell);
                setOnDragEntered(gridCell);
                setOnDragExited(gridCell);
                setOnDragDropped(gridCell);

                Button button = new Button();
                button.setPrefSize(GRID_CELL_SIZE, GRID_CELL_SIZE);
                button.setStyle("-fx-background-radius: 0");
                gridCell.getChildren().add(button);
                activePlayerGrid.add(gridCell, x + 1, y + 1);
            }
        }

        // Ship placing box.
        // Make ship buttons and properly size.
        Button carrier = new Button("Carrier");
        Button battleship = new Button("Battleship");
        Button cruiser = new Button("Cruiser");
        Button submarine = new Button("Submarine");
        Button destroyer = new Button("Destroyer");

        carrier.setPrefSize(GRID_CELL_SIZE * 5, GRID_CELL_SIZE);
        battleship.setPrefSize(GRID_CELL_SIZE * 4, GRID_CELL_SIZE);
        cruiser.setPrefSize(GRID_CELL_SIZE * 3, GRID_CELL_SIZE);
        submarine.setPrefSize(GRID_CELL_SIZE * 3, GRID_CELL_SIZE);
        destroyer.setPrefSize(GRID_CELL_SIZE * 2, GRID_CELL_SIZE);

        shipPlacingBox.getChildren().addAll(new Label("Ships to Place"), carrier, battleship, cruiser, submarine, destroyer);

        // TODO: Handling dragging - look over this section.
        setOnDragDetected(carrier);
        setOnDragDetected(battleship);
        setOnDragDetected(cruiser);
        setOnDragDetected(submarine);
        setOnDragDetected(destroyer);

        setOnDragDone(carrier);
        setOnDragDone(battleship);
        setOnDragDone(cruiser);
        setOnDragDone(submarine);
        setOnDragDone(destroyer);

        preparationLayout.setLeft(activePlayerGrid);
        preparationLayout.setRight(shipPlacingBox);

        return preparationLayout;
    }


    //TODO: Event handlers - clean these up.
    public void setOnDragDetected(Button source)
    {
        source.setOnDragDetected((MouseEvent event) -> {
            Dragboard db = source.startDragAndDrop(TransferMode.ANY);

            /* put a string on dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString(source.getText() + "\n" + source.getWidth() / GRID_CELL_SIZE);
            db.setContent(content);

            event.consume();
        });
    }

    public void setOnDragDone(Button source)
    {
        source.setOnDragDone((DragEvent event) -> {
            /* If the ship was successfully placed, clear the ship button from ships to place,
               and update the grid with the ship visible.
             */
            if (event.getTransferMode() == TransferMode.MOVE) {
                source.setVisible(false);
                source.setCancelButton(true);
            }

            event.consume();
        });
    }

    //target event handlers
    public void setOnDragOver(StackPane target)
    {
        target.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != target
                    && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        });
    }

    public void setOnDragEntered(StackPane target)
    {
        target.setOnDragEntered((DragEvent event) -> {
            if (event.getGestureSource() != target
                    && event.getDragboard().hasString()) {
                target.setStyle("-fx-background-color: green;");
            }

            event.consume();
        });
    }

    public void setOnDragExited(StackPane target)
    {
        target.setOnDragExited((DragEvent event) -> {
            target.setStyle("-fx-background-color: transparent;");

            event.consume();
        });
    }

    public void setOnDragDropped(StackPane target)
    {
        target.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            // TODO: This is quite messy.
            Button buttonAtTarget = (Button) target.getChildren().get(target.getChildren().size() - 1);
            if (db.hasString() && buttonAtTarget.getText().isEmpty()) {
                // Check to ensure nearby buttons are not occupied.
                GridPane grid = (GridPane) target.getParent();
                int xCoord = GridPane.getColumnIndex(target) - 1; // as the letter and numbers on the side add 1.
                int yCoord = GridPane.getRowIndex(target) - 1;
                String[] buttonData = db.getString().split("\n");
                boolean canFit = true;
                // Todo: currently only does rightwards
                for (int i = 0; i < Math.round(Double.parseDouble(buttonData[1])); i++) {
                    if (playerBoard.coordinateOutsideBoard(new Coordinate(xCoord + i, yCoord)) || occupiedCoords[yCoord][xCoord + i]) {
                        canFit = false;
                        break;
                    }
                }

                if (canFit) {
                    shipsPlaced++;

                    // Communicate to nearby buttons
                    for (int i = 0; i < Math.round(Double.parseDouble(buttonData[1])); i++) { // minus 1 as we already did the current square.
                        StackPane cell = (StackPane) getNodeFromGridPane(grid, xCoord + i + 1, yCoord + 1);
                        if (cell != null) {
                            Button cellButton = (Button) cell.getChildren().get(cell.getChildren().size() - 1);
                            cellButton.setText("S" + shipsPlaced);
                            occupiedCoords[yCoord][xCoord + i] = true;
                        } else {
                            throw new RuntimeException("oops");
                        }
                    }

                    success = true;
                }
            }

            event.setDropCompleted(success);

            event.consume();
        });
    }


    private Node getNodeFromGridPane(GridPane gridPane, int column, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }

        return null;
    }
}
