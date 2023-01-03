module com.example.battleship {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.battleship to javafx.fxml;
    exports com.example.battleship;

    opens battleship.ui to javafx.fxml;
    exports battleship.ui;
}