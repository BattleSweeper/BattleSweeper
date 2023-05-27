module dev.battlesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.reactivex.rxjava3;
    requires org.slf4j;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;
    requires static lombok;
    requires org.java_websocket;
    requires com.auth0.jwt;


    opens dev.battlesweeper to javafx.fxml;
    exports dev.battlesweeper;
    exports dev.battlesweeper.objects;
    opens dev.battlesweeper.objects to javafx.fxml;
    exports dev.battlesweeper.scenes;
    opens dev.battlesweeper.scenes to javafx.fxml;
    exports dev.battlesweeper.network.body;
    opens dev.battlesweeper.network.body to com.fasterxml.jackson.databind;
    exports dev.battlesweeper.objects.packet;
    opens dev.battlesweeper.objects.packet to com.fasterxml.jackson.databind;
    exports dev.battlesweeper.objects.json;
    opens dev.battlesweeper.objects.json to com.fasterxml.jackson.databind;
}