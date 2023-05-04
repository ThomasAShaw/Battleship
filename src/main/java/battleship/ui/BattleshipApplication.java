package battleship.ui;

import battleship.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BattleshipApplication extends Application {
    private Game currentGame;
    private Stage stage;
    private Scene activeScene;
    private PlayerView playerOne;
    private PlayerView playerTwo;
    private MainMenuView mainMenu = new MainMenuView(this);
    private boolean isPlayerOneTurn;

    @Override
    public void start(Stage window) throws Exception {
        this.stage = window;

        // Set the minimum window size.
        this.stage.setMinWidth(300);
        this.stage.setMinHeight(300);

        this.isPlayerOneTurn = true;
        this.activeScene = getMainMenuView();
        window.setScene(activeScene);
        window.show();
    }

    public static void main(String[] args) {
        launch(BattleshipApplication.class);
    }

    /**
     * Helper method for getting the main-menu view.
     * @return the main-menu scene.
     */
    private Scene getMainMenuView() {
        return mainMenu.getMainMenuView();
    }

    void beginNewGame() {
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
        endMenu.getStyleClass().add("end-menu");

        Label winnerLabel = new Label( " Wins!");
        // FIXME: Label winnerLabel = new Label(currentGame.checkWinner() + " Wins!");
        winnerLabel.getStyleClass().add("winner-label");

        Button mainMenuButton = new Button("Back to Main Menu");
        mainMenuButton.getStyleClass().add("back-to-main-menu-button");
        mainMenuButton.setOnAction((event) -> switchScene(getMainMenuView()));

        endMenu.getChildren().addAll(winnerLabel, mainMenuButton);
        Scene gameOverScene = new Scene(endMenu, 1000, 750);
        gameOverScene.getStylesheets().add(getClass().getResource("/battleship.css").toExternalForm());

        switchScene(gameOverScene);
    }
}
