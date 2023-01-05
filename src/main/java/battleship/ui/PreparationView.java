package battleship.ui;

import battleship.Board;
import battleship.Coordinate;
import battleship.Game;
import battleship.Ship;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.*;

public class PreparationView {
    // Specific to one player, for one game.
    private final Game game;
    public static final double GRID_CELL_SIZE = PlayerView.GRID_CELL_SIZE;
    private final List<Ship> placedShips = new ArrayList<>();
    private final List<Ship> allShips;
    private final Set<Coordinate> occupiedCoords = new HashSet<>();
    private final Map<Coordinate, StackPane> gridCells = new HashMap<>();
    private boolean isHorizontal = true;
    private BorderPane preparationLayout; // FIXME: potentially remove?
    private List<Button> shipButtons = new ArrayList<>(); // FIXME: potentially remove?
    private final boolean isPlayerOne;
    private Label statusLabel; // FIXME: potentially remove?

    public PreparationView(Game game, boolean isPlayerOne) {
        this.game = game;
        this.isPlayerOne = isPlayerOne;
        this.allShips = game.getShips(isPlayerOne);
    }

    /**
     * Get the window for when a player is setting their ships' positions.
     * @return the window of the preparation view.
     */
    public Parent getPreparationView() {
        preparationLayout = new BorderPane();
        GridPane activePlayerGrid = new GridPane();
        VBox sideMenu = new VBox();
        /* Ships are horizontal on default. */
        VBox shipBox = new VBox();

        /* Create player board grid. */
        for (int i = 0; i < Board.DEFAULT_SIZE; i++) {
            activePlayerGrid.getColumnConstraints().add(new ColumnConstraints(GRID_CELL_SIZE));
            activePlayerGrid.getRowConstraints().add(new RowConstraints(GRID_CELL_SIZE));
        }

        /* Set letter and number labels on side of player grid. */
        for (int i = 0; i < game.getBoardWidth(isPlayerOne) + 1; i++) {
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

        /* Add buttons to player grid. */
        for (int y = 0; y < game.getBoardHeight(isPlayerOne); y++) {
            for (int x = 0; x < game.getBoardWidth(isPlayerOne); x++) {
                /* For dragging functionality... */
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
                /* Add one as letter and number cells take up first row and column, respectively. */
                gridCells.put(new Coordinate(x + 1, y + 1), gridCell);
            }
        }

        /* Ship placing box. */
        setShipButtons();

        shipBox.getChildren().add(new Label("Ships to Place"));
        shipBox.getChildren().addAll(shipButtons);

        HBox sideMenuButtons = new HBox();
        Button rotateButton = new Button("Rotate");
        Button resetButton = new Button("Reset");
        Button confirmButton = new Button("Confirm");
        statusLabel = new Label("");

        rotateButton.setOnAction((event) -> swapShipOrientation());
        resetButton.setOnAction((event) -> resetPlacement());
        confirmButton.setOnAction((event) -> {
            boolean success = confirmShipPlacement();

            if (success) {
                statusLabel.setStyle("-fx-background-color: green;");
                statusLabel.setText("Successfully confirmed placement.");

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e ->{
                    BattleshipApplication.isPlayerOneTurn = !isPlayerOne;
                    BattleshipApplication.switchPlayerScene();
                });
                pause.play();
            } else {
                statusLabel.setStyle("-fx-background-color: red;");
                statusLabel.setText("Error confirming placement.");
            }
        });

        sideMenuButtons.getChildren().addAll(rotateButton, resetButton, confirmButton);


        sideMenu.getChildren().addAll(sideMenuButtons, statusLabel, shipBox);

        preparationLayout.setLeft(activePlayerGrid);
        preparationLayout.setRight(sideMenu);

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
            if (isHorizontal) {
                content.putString(sourceShip.getText() + "\n" + sourceShip.getWidth() / GRID_CELL_SIZE);
            } else {
                content.putString(sourceShip.getText() + "\n" + sourceShip.getHeight() / GRID_CELL_SIZE);
            }

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
                int xCoord = GridPane.getColumnIndex(targetCell) - 1; // As left number column is at position 0.
                int yCoord = GridPane.getRowIndex(targetCell) - 1; // As top letter row is at position 0.
                String[] shipData = db.getString().split("\n"); // Copied cell data is stored in String format, separated by newline.
                int shipLength = (int) Double.parseDouble(shipData[1]);

                if (canPlaceHere(new Coordinate(xCoord, yCoord), shipLength)) {
                    int shipsPlaced = placedShips.size() + 1;

                    /* Set all required cells to signify occupied by ship. */
                    // TODO: Currently only does rightwards and downwards - issue with coords...?
                    for (int i = 0; i < shipLength; i++) {
                        if (isHorizontal) {
                            StackPane cell =  gridCells.get(new Coordinate(xCoord + i + 1, yCoord + 1));
                            if (cell != null) {
                                Button cellButton = (Button) cell.getChildren().get(cell.getChildren().size() - 1);
                                cellButton.setText("S" + shipsPlaced);
                                occupiedCoords.add(new Coordinate(xCoord + i, yCoord));
                            } else {
                                throw new RuntimeException("Null cell.");
                            }
                        } else {
                            StackPane cell =  gridCells.get(new Coordinate(xCoord + 1, yCoord + i + 1));
                            if (cell != null) {
                                Button cellButton = (Button) cell.getChildren().get(cell.getChildren().size() - 1);
                                cellButton.setText("S" + shipsPlaced);
                                occupiedCoords.add(new Coordinate(xCoord, yCoord + i));
                            } else {
                                throw new RuntimeException("Null cell.");
                            }
                        }
                    }

                    /* Add ship to placedShips */
                    System.out.println(xCoord + " " + yCoord);
                    if (isHorizontal) {
                        placedShips.add(new Ship(shipData[0], new Coordinate(xCoord, yCoord), new Coordinate(xCoord + shipLength - 1, yCoord)));
                        // System.out.println((xCoord + shipLength -1) + " " + yCoord);
                    } else {
                        placedShips.add(new Ship(shipData[0], new Coordinate(xCoord, yCoord), new Coordinate(xCoord, yCoord + shipLength - 1)));
                        // System.out.println(xCoord + " " + (yCoord + shipLength -1));
                    }

                    statusLabel.setText("");
                    statusLabel.setStyle(null);
                    success = true;
                }

                if (!success) {
                    statusLabel.setText("Error placing ship here.");
                    statusLabel.setStyle("-fx-background-color: red;");
                }
            }

            /* Communicate if drag & drop was completed successfully. */
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Determine if ship placement is valid.
     * @param startCoordinate starting position of placement; not null.
     * @param shipLength length of the ship.
     * @return true if ship placement is valid, false otherwise.
     */
    private boolean canPlaceHere(Coordinate startCoordinate, int shipLength) {
        Set<Coordinate> coordSet = getPotentialCoords(startCoordinate, shipLength);

        for (Coordinate coord : coordSet) {
            if (!game.isValidCoordinate(coord, isPlayerOne)
                    || occupiedCoords.contains(coord)) {
                return false;
            }
        }

        return true;
    }

    // TODO: Only supports rightwards and downwards placement
    /**
     * Get all the coordinates this ship will occupy.
     * @param startCoordinate starting position of placement; not null.
     * @param shipLength length of the ship.
     * @return list of the coordinates this ship will sit on.
     */
    private Set<Coordinate> getPotentialCoords(Coordinate startCoordinate, int shipLength) {
        Set<Coordinate> coordSet = new HashSet<>();
        int xCoord = startCoordinate.getX();
        int yCoord = startCoordinate.getY();

        if (isHorizontal) {
            for (int i = 0; i < shipLength; i++) {
                coordSet.add(new Coordinate(xCoord + i, yCoord));
            }
        } else {
            for (int i = 0; i < shipLength; i++) {
                coordSet.add(new Coordinate(xCoord, yCoord + i));
            }
        }

        return coordSet;
    }

    // TODO: Fix button text.
    private void swapShipOrientation() {
        VBox sideMenu = (VBox) preparationLayout.getRight();
        sideMenu.getChildren().remove(sideMenu.getChildren().size() - 1);
        if (isHorizontal) {
            isHorizontal = false;
            HBox shipBoxVertical = new HBox();
            for (Button ship : shipButtons) {
                ship.setPrefSize(ship.getHeight(), ship.getWidth());
                shipBoxVertical.getChildren().add(ship);
            }
            sideMenu.getChildren().add(shipBoxVertical);
        } else {
            isHorizontal = true;
            VBox shipBoxHorizontal = new VBox();
            for (Button ship : shipButtons) {
                ship.setPrefSize(ship.getHeight(), ship.getWidth());
                shipBoxHorizontal.getChildren().add(ship);
            }
            sideMenu.getChildren().add(shipBoxHorizontal);
        }
    }

    /**
     * Resets the view by setting everything modified back to its original way.
     */
    private void resetPlacement() {
        /* Reset all saved ships, coordinates, and reset ship buttons. */
        placedShips.clear();
        occupiedCoords.clear();
        setShipButtons();

        /* Reset gridCells. */
        for(StackPane gridCell : gridCells.values()) {
            Button gridButton = (Button) gridCell.getChildren().get(gridCell.getChildren().size() - 1);
            gridButton.setText("");
        }

        /* Reset all labels and sideMenu. */
        statusLabel.setText("");
        statusLabel.setStyle(null);

        VBox sideMenu = (VBox) preparationLayout.getRight();
        sideMenu.getChildren().remove(sideMenu.getChildren().size() - 1);
        VBox shipBox = new VBox();
        shipBox.getChildren().add(new Label("Ships to Place"));
        shipBox.getChildren().addAll(shipButtons);
        sideMenu.getChildren().add(shipBox);
    }

    /**
     * Confirm the placement of ships after being dragged onto board.
     * @return true if ships were successfully set, false otherwise.
     */
    private boolean confirmShipPlacement() {
        if (placedShips.size() != allShips.size()) {
            return false;
        }

        for (Ship ship : placedShips) {
            if (!game.setShip(isPlayerOne, ship)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sets/resets all ship buttons back to their original way.
     */
    private void setShipButtons () {
        isHorizontal = true;
        shipButtons.clear();
        for (Ship ship : allShips) {
            // Make ship buttons and properly size, horizontal on default.
            Button button = new Button(ship.getName());
            button.setPrefSize(GRID_CELL_SIZE * ship.getShipLength(), GRID_CELL_SIZE);

            // For drag & drop functionality.
            setOnDragDetected(button);
            setOnDragDone(button);

            shipButtons.add(button);
        }
    }
}
