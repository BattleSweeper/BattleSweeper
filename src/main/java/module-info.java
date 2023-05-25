module dev.battlesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.reactivex.rxjava3;
    requires org.slf4j;
    requires static lombok;


    opens dev.battlesweeper to javafx.fxml;
    exports dev.battlesweeper;
    exports dev.battlesweeper.objects;
    opens dev.battlesweeper.objects to javafx.fxml;
}