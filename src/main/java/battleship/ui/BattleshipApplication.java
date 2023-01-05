package battleship.ui;

import battleship.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BattleshipApplication extends Application {
    private static Game currentGame;
    public static boolean isPlayerOneTurn = true;
    public static BorderPane activePane = new BorderPane();
    private static PlayerView playerOne, playerTwo;

    @Override
    public void start(Stage window) throws Exception {
        activePane.setCenter(getMainMenuView());

        Scene scene = new Scene(activePane);

        window.setScene(scene);
        window.show();
    }

    private StackPane createView(String text) {

        StackPane layout = new StackPane();
        layout.setPrefSize(300, 180);
        layout.getChildren().add(new Label(text));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    public static void main(String[] args) {
        launch(BattleshipApplication.class);
    }

    // Main Menu Scene
    private Pane getMainMenuView() {
        Pane layout = new StackPane();

        // TODO: Finish text fonts
        // Using comic sans as placeholder for now.
        Label title = new Label("Battleship");
        Label subtitle = new Label("Created By: Me");
        title.setFont(new Font("Agency FB Bold", 40));
        subtitle.setFont(new Font("Comic Sans MS", 15));

        // TODO: move this const somewhere better
        final double buttonWidth = 100;
        Button newGameButton = new Button("New Game");
        Button quitButton = new Button("Quit");
        newGameButton.setFont(new Font("Comic Sans MS", 15));
        quitButton.setFont(new Font("Comic Sans MS", 15));
        newGameButton.setMinWidth(buttonWidth);
        quitButton.setMinWidth(buttonWidth);

        newGameButton.setOnAction((event) -> beginNewGame());
        quitButton.setOnAction((event) -> Platform.exit());


        VBox menu = new VBox();
        menu.setSpacing(10);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(10));
        menu.getChildren().addAll(title, subtitle, newGameButton, quitButton);

        layout.getChildren().add(menu);

        return layout;
    }

    private void beginNewGame() {
        currentGame = new Game();
        playerOne = new PlayerView(currentGame, true);
        playerTwo = new PlayerView(currentGame, false);

        switchPlayerScene();
    }

    public static void switchPlayerScene() {
        if (isPlayerOneTurn) {
            playerOne.getPlayerView();
        } else {
            playerTwo.getPlayerView();
        }
    }
}
