package battleship.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainMenuView {
    private final BattleshipApplication app;

    public MainMenuView(BattleshipApplication app) {
        this.app = app;
    }

    public Scene getMainMenuView() {
        VBox mainMenu = new VBox();
        mainMenu.getStyleClass().add("main-menu");

        List<Label> labels = createTitleAndSubtitle();
        List<Button> buttons = createMenuButtons();

        mainMenu.getChildren().addAll(labels);
        mainMenu.getChildren().addAll(buttons);

        Scene mainMenuScene = new Scene(mainMenu, 1000, 750);
        mainMenuScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/battleship.css")).toExternalForm());

        return mainMenuScene;
    }

    private List<Label> createTitleAndSubtitle() {
        List<Label> labels = new ArrayList<>();

        /* Title Label. */
        Label title = new Label("Battleship");
        title.getStyleClass().add("title");
        labels.add(title);

        /* Subtitle Label. */
        Label subtitle = new Label("Created By: Me");
        subtitle.getStyleClass().add("subtitle");
        labels.add(subtitle);

        return labels;
    }

    /**
     * Helper for creating menu buttons.
     * @return list of menu buttons in order.
     */
    private List<Button> createMenuButtons() {
        List<Button> buttons = new ArrayList<>();

        Button localMultiplayerButton = new Button("Local Multiplayer");
        localMultiplayerButton.getStyleClass().add("menu-button");
        localMultiplayerButton.setOnAction(event -> onLocalMultiplayerButtonClick());
        buttons.add(localMultiplayerButton);

        Button optionsButton = new Button("Options");
        optionsButton.getStyleClass().add("menu-button");
        optionsButton.setOnAction(event -> onOptionsButtonClick());
        buttons.add(optionsButton);

        Button quitGameButton = new Button("Quit Game");
        quitGameButton.getStyleClass().add("menu-button");
        quitGameButton.setOnAction(event -> onQuitGameButtonClick());
        buttons.add(quitGameButton);

        return buttons;
    }

    /**
     * Handles "Local Multiplayer" button being clicked.
     */
    private void onLocalMultiplayerButtonClick() {
        app.beginNewGame();
    }

    /**
     * Handles "Options" button being clicked.
     */
    private void onOptionsButtonClick() {
        // TODO: This should open the options menu.
        app.gameOver();
    }

    /**
     * Handles "Quit Game" button being clicked.
     */
    private void onQuitGameButtonClick() {
        Platform.exit();
    }
}
