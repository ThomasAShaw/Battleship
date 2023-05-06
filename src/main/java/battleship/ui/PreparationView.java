package battleship.ui;

import battleship.Board;
import battleship.Coordinate;
import battleship.Game;
import battleship.Ship;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
    private final BattleshipApplication app;

    public PreparationView(BattleshipApplication app, Game game, boolean isPlayerOne) {
        this.app = app;
        this.game = game;
        this.isPlayerOne = isPlayerOne;
        this.allShips = game.getAllShips(isPlayerOne);
    }

    /**
     * Get the window for when a player is setting their ships' positions.
     * @return the window of the preparation view.
     */
    public Scene getPreparationView() {
        preparationLayout = new BorderPane();
        preparationLayout.getStyleClass().add("watery-background");

        /* Active player grid. */
        GridPane activePlayerGrid = initializePlayerGrid(Board.DEFAULT_SIZE);
        activePlayerGrid.setPadding(new Insets(20));

        /* Ship placing box. */
        VBox sideMenu = initializeSideMenu();

        HBox mainContent = new HBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(activePlayerGrid, sideMenu);

        preparationLayout.setCenter(mainContent);

        Scene preparationViewScene = new Scene(preparationLayout, 1000, 750);
        preparationViewScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/battleship.css")).toExternalForm()); //FIXME

        return preparationViewScene;
    }

    /* Event handlers for preparation view. */

    /**
     * Handle ship being dragged by user during preparation view.
     * @param sourceShip ship button that user dragged from.
     */
    private void setOnDragDetected(Button sourceShip) {
        sourceShip.setOnDragDetected((MouseEvent event) -> {
            Dragboard db = sourceShip.startDragAndDrop(TransferMode.ANY);

            /* Put ship data in String format separated by newline. */
            ClipboardContent content = new ClipboardContent();
            if (isHorizontal) {
                content.putString(sourceShip.getText() + "!" + sourceShip.getWidth() / GRID_CELL_SIZE);
            } else {
                content.putString(sourceShip.getText() + "!" + sourceShip.getHeight() / GRID_CELL_SIZE);
            }

            db.setContent(content);

            event.consume();
        });
    }

    /**
     * Handle user finishing DragEvent during preparation view.
     * @param sourceShip ship button that user dragged from.
     */
    private void setOnDragDone(Button sourceShip) {
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
    private void setOnDragOver(StackPane targetCell) {
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
    private void setOnDragEntered(StackPane targetCell) {
        targetCell.setOnDragEntered((DragEvent event) -> {
            /* Add graphical cue to show if user can place ship at targetCell. */
            if (event.getGestureSource() != targetCell && event.getDragboard().hasString()) {
                // FIXME: This is quite messy.
                int xCoord = GridPane.getColumnIndex(targetCell) - 1; // As left number column is at position 0.
                int yCoord = GridPane.getRowIndex(targetCell) - 1; // As top letter row is at position 0.

                Dragboard db = event.getDragboard();
                String[] shipData = db.getString().split("!");
                int shipLength = (int) Math.round(Double.parseDouble(shipData[1]));

                if (canPlaceHere(new Coordinate(xCoord, yCoord), shipLength)) {
                    Set<Coordinate> gridCellCoords = getPotentialCoords(new Coordinate(xCoord + 1, yCoord + 1), shipLength);

                    for (Coordinate cellCoord : gridCellCoords) {
                        // Add visual cue for valid placement.
                        gridCells.get(cellCoord).getChildren().get(targetCell.getChildren().size() - 1)
                                .setStyle("-fx-background-color: rgba(0, 255, 0, 0.5); -fx-border-color: rgba(0, 255, 0, 0.5); -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.4), 5, 0.0, 3, 3); -fx-border-width: 2px;");
                    }
                } else {
                    Set<Coordinate> gridCellCoords = getPotentialCoords(new Coordinate(xCoord + 1, yCoord + 1), shipLength);
                    for (Coordinate cellCoord : gridCellCoords) {
                        StackPane gridCell = gridCells.get(cellCoord);

                        if (gridCell != null) {
                            // Add visual cue for invalid placement.
                            gridCells.get(cellCoord).getChildren().get(targetCell.getChildren().size() - 1)
                                    .setStyle("-fx-background-color: rgba(255, 0, 0, 0.5); -fx-border-color: rgba(255, 0, 0, 0.5); -fx-border-width: 2px;");
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
            String[] shipData = db.getString().split("!");
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
                String[] shipData = db.getString().split("!"); // Copied cell data is stored in String format, separated by exclamation mark.
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
        HBox mainContent = (HBox) preparationLayout.getCenter();
        VBox sideMenu = (VBox) mainContent.getChildren().get((mainContent.getChildren().size() - 1));
        sideMenu.getChildren().remove(sideMenu.getChildren().size() - 1);
        if (isHorizontal) {
            isHorizontal = false;
            HBox shipBoxVertical = new HBox();
            shipBoxVertical.getStyleClass().add("ship-box");
            for (Button ship : shipButtons) {
                ship.setPrefSize(ship.getHeight(), ship.getWidth());
                ship.getStyleClass().remove(ship.getStyleClass().size() - 1);
                ship.getStyleClass().add("side-menu-ship-button-vertical");
                setButtonText(ship, isHorizontal);
                shipBoxVertical.getChildren().add(ship);
            }
            sideMenu.getChildren().add(shipBoxVertical);
        } else {
            isHorizontal = true;
            VBox shipBoxHorizontal = new VBox();
            shipBoxHorizontal.getStyleClass().add("ship-box");
            for (Button ship : shipButtons) {
                ship.setPrefSize(ship.getHeight(), ship.getWidth());
                ship.getStyleClass().remove(ship.getStyleClass().size() - 1);
                ship.getStyleClass().add("side-menu-ship-button-horizontal");
                setButtonText(ship, isHorizontal);
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

        HBox mainContent = (HBox) preparationLayout.getCenter();
        VBox sideMenu = (VBox) mainContent.getChildren().get(mainContent.getChildren().size() - 1);
        sideMenu.getChildren().remove(sideMenu.getChildren().size() - 1);
        Pane shipBox = getShipBox();
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
            double initialFontSize = calculateFontSize(button, button.getText());
            button.setStyle("-fx-font-size: " + initialFontSize + "px;");

            // Update the font size when the button is resized
            button.widthProperty().addListener((obs, oldVal, newVal) -> {
                double newFontSize = calculateFontSize(button, button.getText());
                button.setStyle("-fx-font-size: " + newFontSize + "px;");
            });

            button.heightProperty().addListener((obs, oldVal, newVal) -> {
                double newFontSize = calculateFontSize(button, button.getText());
                button.setStyle("-fx-font-size: " + newFontSize + "px;");
            });

            button.getStyleClass().add("side-menu-ship-button-horizontal");
            shipButtons.add(button);
        }
    }

    /**
     * Helper for creating the active player grid.
     * @param boardSize number of grid cells horizontally and vertically the board will be.
     * @return a VBox representing the physical grid for the player.
     */
    private GridPane initializePlayerGrid(int boardSize) {
        GridPane activePlayerGrid = new GridPane();

        /* Create player board grid. */
        for (int i = 0; i < boardSize; i++) {
            activePlayerGrid.getColumnConstraints().add(new ColumnConstraints(GRID_CELL_SIZE));
            activePlayerGrid.getRowConstraints().add(new RowConstraints(GRID_CELL_SIZE));
        }

        /* Set letter and number labels on side of player grid. */
        for (int i = 0; i < game.getBoardWidth(isPlayerOne) + 1; i++) {
            Label letterLabel = new Label(i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : "*");
            Label numberLabel = new Label(i > 0 ? Integer.toString(i) : "*");
            letterLabel.getStyleClass().add("grid-label");
            numberLabel.getStyleClass().add("grid-label");

            letterLabel.setPrefWidth(GRID_CELL_SIZE);
            letterLabel.setPrefHeight(GRID_CELL_SIZE);
            letterLabel.setAlignment(Pos.CENTER);

            numberLabel.setPrefWidth(GRID_CELL_SIZE);
            numberLabel.setPrefHeight(GRID_CELL_SIZE);
            numberLabel.setAlignment(Pos.CENTER);

            if (i != 0) {
                activePlayerGrid.add(letterLabel, i, 0);
                activePlayerGrid.add(numberLabel, 0, i);
            } else {
                activePlayerGrid.add(letterLabel, i, i);
            }
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
                button.getStyleClass().add("grid-button");
                gridCell.getChildren().add(button);
                activePlayerGrid.add(gridCell, x + 1, y + 1);
                /* Add one as letter and number cells take up first row and column, respectively. */
                gridCells.put(new Coordinate(x + 1, y + 1), gridCell);
            }
        }

        return activePlayerGrid;
    }

    /**
     * Helper for creating the side-menu.
     * @return a VBox containing the finished side-menu.
     */
    private VBox initializeSideMenu() {
        VBox sideMenu = new VBox();
        sideMenu.getStyleClass().add("side-menu");

        Pane shipBox = getShipBox(); // Ships are horizontal on default.

        HBox sideMenuButtons = new HBox(10);
        sideMenuButtons.setAlignment(Pos.CENTER);

        Button rotateButton = new Button("Rotate");
        rotateButton.getStyleClass().add("side-menu-button");
        Button resetButton = new Button("Reset");
        resetButton.getStyleClass().add("side-menu-button");
        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("side-menu-button");

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("side-menu-label");

        rotateButton.setOnAction((event) -> swapShipOrientation());
        resetButton.setOnAction((event) -> resetPlacement());
        confirmButton.setOnAction((event) -> {
            boolean success = confirmShipPlacement();

            if (success) {
                statusLabel.setStyle("-fx-background-color: green;");
                statusLabel.setText("Successfully confirmed placement.");

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> app.switchPlayer());
                pause.play();
            } else {
                statusLabel.setStyle("-fx-background-color: red;");
                statusLabel.setText("Error confirming placement.");
            }
        });

        sideMenuButtons.getChildren().addAll(rotateButton, resetButton, confirmButton);

        sideMenu.getChildren().addAll(sideMenuButtons, statusLabel, shipBox);

        return sideMenu;
    }

    /**
     * Get a new ShipBox, when resetting the board. Uses default horizontal ship position.
     * @return new ShipBox setup and ready for use.
     */
    private Pane getShipBox() {
        Pane shipBox = new VBox(10);
        shipBox.getStyleClass().add("ship-box");
        setShipButtons();

        shipBox.getChildren().addAll(shipButtons);

        return shipBox;
    }

    private void setButtonText(Button button, boolean isHorizontal) {
        if (isHorizontal) {
            String horizontalText = button.getText();
            button.setText(horizontalText.replace("\n", ""));
        } else {
            String buttonText = button.getText();
            StringBuilder verticalText = new StringBuilder();
            for (int i = 0; i < buttonText.length(); i++) {
                verticalText.append(buttonText.charAt(i));
                if (i < buttonText.length() - 1) {
                    verticalText.append('\n');
                }
            }
            button.setText(verticalText.toString());
        }
    }

    /**
     * Helper for getting the size the ship button font should be.
     * @param button to check.
     * @param text that is displayed on button.
     * @return appropriate font size.
     */
    private double calculateFontSize(Button button, String text) {
        double buttonWidth = button.getWidth();
        double buttonHeight = button.getHeight();

        double maxFontSizeWidth;
        double maxFontSizeHeight;

        if (!isHorizontal) {
            maxFontSizeWidth = buttonWidth;
            maxFontSizeHeight = buttonHeight / text.length();
        } else {
            maxFontSizeWidth = buttonWidth / text.length();
            maxFontSizeHeight = buttonHeight;
        }

        double fontSize = Math.min(maxFontSizeWidth, maxFontSizeHeight);

        double minFontSize = 4;
        double maxFontSize = 20;

        fontSize = Math.max(minFontSize, fontSize);
        fontSize = Math.min(maxFontSize, fontSize);

        return fontSize;
    }
}
