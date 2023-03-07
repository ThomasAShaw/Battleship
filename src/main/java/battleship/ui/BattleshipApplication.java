package battleship.ui;

import battleship.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BattleshipApplication extends Application {
    private Game currentGame;
    private Stage stage;
    private Scene activeScene;
    private PlayerView playerOne;
    private PlayerView playerTwo;
    private boolean isPlayerOneTurn;

    @Override
    public void start(Stage window) throws Exception {
        this.stage = window;
        this.isPlayerOneTurn = true;
        this.activeScene = getMainMenuView();
        window.setScene(activeScene);
        window.show();
    }

    public static void main(String[] args) {
        launch(BattleshipApplication.class);
    }

    // Main Menu Scene
    private Scene getMainMenuView() {
        VBox mainMenu = new VBox();
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setSpacing(10);
        mainMenu.setStyle("-fx-background-color: lightblue;");

        Scene mainMenuScene = new Scene(mainMenu);

        // Using comic sans as placeholder for now.
        Label title = new Label("Battleship");
        Label subtitle = new Label("Created By: Me");
        title.setFont(new Font("Agency FB Bold", 40));
        subtitle.setFont(new Font("Comic Sans MS", 15));
        mainMenu.getChildren().addAll(title, subtitle);

        // TODO: move this const somewhere better
        final double buttonWidth = 100;
        Button localMButton = new Button("Local Multiplayer");
        Button optionsButton = new Button("Options");
        Button quitButton = new Button("Quit Game");

        localMButton.setFont(new Font("Comic Sans MS", 15));
        optionsButton.setFont(new Font("Comic Sans MS", 15));
        quitButton.setFont(new Font("Comic Sans MS", 15));

        localMButton.setMinWidth(buttonWidth);
        optionsButton.setMinWidth(buttonWidth);
        quitButton.setMinWidth(buttonWidth);

        localMButton.setOnAction((event) -> beginNewGame());
        quitButton.setOnAction((event) -> Platform.exit());

        mainMenu.getChildren().addAll(localMButton, optionsButton, quitButton);

        return mainMenuScene;
    }

    private void beginNewGame() {
        currentGame = new Game();
        playerOne = new PlayerView(this, currentGame, true);
        playerTwo = new PlayerView(this, currentGame, false);

        switchScene(playerOne.getPlayerView());
    }

    public void switchScene(Scene scene) {
        activeScene = scene;
        stage.setScene(activeScene);
    }

    public void switchPlayer() {
        isPlayerOneTurn = !isPlayerOneTurn;
        switchScene(isPlayerOneTurn ? playerOne.getPlayerView() : playerTwo.getPlayerView());
    }

    // TODO: Implement this to look nicer.
    public void gameOver() {
        VBox endMenu = new VBox();
        endMenu.setAlignment(Pos.CENTER);
        Label winnerLabel = new Label(currentGame.checkWinner() + " Wins!");
        Button mainMenuButton = new Button("Back to Main Menu");
        mainMenuButton.setOnAction((event) -> switchScene(getMainMenuView()));

        endMenu.getChildren().addAll(winnerLabel, mainMenuButton);
        Scene gameOverScene = new Scene(endMenu);
        switchScene(gameOverScene);
    }
}
