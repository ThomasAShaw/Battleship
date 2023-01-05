package battleship.ui;

import battleship.Board;
import battleship.Coordinate;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreparationView {
    // TODO: placing ships, shooting ships, and waiting.
    // TODO: unplaced ship list.
    // Specific to one player, for one game.
    private final Board playerBoard;
    public static final double GRID_CELL_SIZE = 50;
    private int shipsPlaced = 0;
    private Set<Coordinate> occupiedCoords;
    private final Map<Coordinate, StackPane> gridCells;

    public PreparationView(Board playerBoard) {
        this.playerBoard = playerBoard;
        occupiedCoords = new HashSet<>();
        gridCells = new HashMap<>();
    }

    /**
     * Get the window for when a player is setting their ships' positions.
     *
     * @param isPlayerOne true if player one is playing currently, false for player two.
     * @return the window of the preparation view.
     */
    public Parent getPreparationView() {

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
                // Add one as letter and number cells take up first row and column, respectively.
                gridCells.put(new Coordinate(x + 1, y + 1), gridCell);
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

    /* Event handlers for preparation view. */

    /**
     * Handle ship being dragged by user during preparation view.
     * @param sourceShip ship button that user dragged from.
     */
    public void setOnDragDetected(Button sourceShip) {
        sourceShip.setOnDragDetected((MouseEvent event) -> {
            Dragboard db = sourceShip.startDragAndDrop(TransferMode.ANY);

            /* Put ship data in String format separated by newline. */
            ClipboardContent content = new ClipboardContent();
            content.putString(sourceShip.getText() + "\n" + sourceShip.getWidth() / GRID_CELL_SIZE);
            db.setContent(content);

            event.consume();
        });
    }

    /**
     * Handle user finishing DragEvent during preparation view.
     * @param sourceShip ship button that user dragged from.
     */
    public void setOnDragDone(Button sourceShip) {
        sourceShip.setOnDragDone((DragEvent event) -> {
            /* If the ship was successfully placed, clear the ship button from ships to place,
               and update the grid with the ship visible.
             */
            if (event.getTransferMode() == TransferMode.MOVE) {
                sourceShip.setVisible(false);
                sourceShip.setCancelButton(true);
            }

            event.consume();
        });
    }

    /**
     * Handle user dragging ship over targetCell during preparation view.
     * @param targetCell the grid cell user dragged ship over.
     */
    public void setOnDragOver(StackPane targetCell) {
        targetCell.setOnDragOver((DragEvent event) -> {
            /* Accept only if not from same node, and DragBoard has data. */
            if (event.getGestureSource() != targetCell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        });
    }

    /**
     * Handle user moving mouse into targetCell while dragging ship in preparation view.
     * @param targetCell the grid cell that user moved mouse into from while dragging.
     */
    public void setOnDragEntered(StackPane targetCell) {
        targetCell.setOnDragEntered((DragEvent event) -> {
            /* Add graphical cue to show if user can place ship at targetCell. */
            if (event.getGestureSource() != targetCell && event.getDragboard().hasString()) {
                // FIXME: This is quite messy.
                GridPane grid = (GridPane) targetCell.getParent();
                int xCoord = GridPane.getColumnIndex(targetCell) - 1; // As left number column is at position 0.
                int yCoord = GridPane.getRowIndex(targetCell) - 1; // As top letter row is at position 0.

                Dragboard db = event.getDragboard();
                String[] shipData = db.getString().split("\n");
                int shipLength = (int) Math.round(Double.parseDouble(shipData[1]));

                if (canPlaceHere(new Coordinate(xCoord, yCoord), shipLength)) {
                    Set<Coordinate> gridCellCoords = getPotentialCoords(new Coordinate(xCoord + 1, yCoord + 1), shipLength);

                    for (Coordinate cellCoord : gridCellCoords) {
                        gridCells.get(cellCoord).getChildren().get(targetCell.getChildren().size() - 1).setStyle("-fx-background-color: green;");
                    }
                } else {
                    Set<Coordinate> gridCellCoords = getPotentialCoords(new Coordinate(xCoord + 1, yCoord + 1), shipLength);
                    for (Coordinate cellCoord : gridCellCoords) {
                        StackPane gridCell = gridCells.get(cellCoord);

                        if (gridCell != null) {
                            gridCell.getChildren().get(targetCell.getChildren().size() - 1).setStyle("-fx-background-color: red;");
                        }
                    }
                }
            }

            event.consume();
        });
    }

    /**
     * Handle user moving mouse away from targetCell while dragging ship in preparation view.
     * @param targetCell the grid cell that user moved mouse away from while dragging.
     */
    private void setOnDragExited(StackPane targetCell) {
        targetCell.setOnDragExited((DragEvent event) -> {
            /* When user moves mouse off cell, remove graphical cues. */
            // FIXME: This is quite messy.
            GridPane grid = (GridPane) targetCell.getParent();
            int xCoord = GridPane.getColumnIndex(targetCell) - 1; // As left number column is at position 0.
            int yCoord = GridPane.getRowIndex(targetCell) - 1; // As top letter row is at position 0.
            Dragboard db = event.getDragboard();
            String[] shipData = db.getString().split("\n");
            int shipLength = (int) Math.round(Double.parseDouble(shipData[1]));

            Set<Coordinate> gridCellCoords = getPotentialCoords(new Coordinate(xCoord + 1, yCoord + 1), shipLength);
            for (Coordinate cellCoord : gridCellCoords) {
                StackPane gridCell = gridCells.get(cellCoord);

                if (gridCell != null) {
                    gridCell.getChildren().get(targetCell.getChildren().size() - 1).setStyle(null);
                }
            }

            event.consume();
        });
    }

    /**
     * Handle user letting go of dragged ship onto targetCell in preparation view.
     * @param targetCell the grid cell that the user released the dragged ship onto.
     */
    private void setOnDragDropped(StackPane targetCell) {
        targetCell.setOnDragDropped((DragEvent event) -> {
            /* Check to ensure data is stored on DragBoard. */
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                /* Check to ensure target cell and any other required cells are not occupied. */
                GridPane grid = (GridPane) targetCell.getParent();
                int xCoord = GridPane.getColumnIndex(targetCell) - 1; // As left number column is at position 0.
                int yCoord = GridPane.getRowIndex(targetCell) - 1; // As top letter row is at position 0.
                String[] shipData = db.getString().split("\n"); // Copied cell data is stored in String format, separated by newline.

                // TODO: Currently only does rightwards.
                if (canPlaceHere(new Coordinate(xCoord, yCoord), (int) Math.round(Double.parseDouble(shipData[1])))) {
                    shipsPlaced++;

                    /* Set all required cells to signify occupied by ship. */
                    // TODO: Currently only does rightwards.
                    for (int i = 0; i < Double.parseDouble(shipData[1]); i++) {
                        StackPane cell =  gridCells.get(new Coordinate(xCoord + i + 1, yCoord + 1));
                        if (cell != null) {
                            Button cellButton = (Button) cell.getChildren().get(cell.getChildren().size() - 1);
                            cellButton.setText("S" + shipsPlaced);
                            occupiedCoords.add(new Coordinate(xCoord + i, yCoord));
                        } else {
                            throw new RuntimeException("Null cell.");
                        }
                    }

                    success = true;
                }
            }

            /* Communicate if drag & drop was completed successfully. */
            event.setDropCompleted(success);
            event.consume();
        });
    }

    // TODO: Only supports rightwards placement.
    /**
     * Determine if ship placement is valid.
     * @param startCoordinate starting position of placement; not null.
     * @param shipLength length of the ship.
     * @return true if ship placement is valid, false otherwise.
     */
    private boolean canPlaceHere(Coordinate startCoordinate, int shipLength) {
        Set<Coordinate> coordSet = getPotentialCoords(startCoordinate, shipLength);

        for (Coordinate coord : coordSet) {
            if (playerBoard.coordinateOutsideBoard(coord)
                    || occupiedCoords.contains(coord)) {
                return false;
            }
        }

        return true;
    }

    // TODO: Only supports rightwards placement.
    /**
     * Get all the coordinates this ship will sit on.
     * @param startCoordinate starting position of placement; not null.
     * @param shipLength length of the ship.
     * @return list of the coordinates this ship will sit on.
     */
    private Set<Coordinate> getPotentialCoords(Coordinate startCoordinate, int shipLength) {
        Set<Coordinate> coordSet = new HashSet<>();
        int xCoord = startCoordinate.getX();
        int yCoord = startCoordinate.getY();

        for (int i = 0; i < shipLength; i++) {
            coordSet.add(new Coordinate(xCoord + i, yCoord));
        }

        return coordSet;
    }
}
