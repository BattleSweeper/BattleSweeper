module dev.battlesweeper {
    requires javafx.controls;
    requires javafx.fxml;


    opens dev.battlesweeper to javafx.fxml;
    exports dev.battlesweeper;
}