package dev.battlesweeper;

//시간 함수를 fxml과 App에 연동하는 부분.
//불러온 숫자를 문자열로 처리해서 입력한다.
import dev.battlesweeper.utils.FontUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    Time time = new Time("000");

    @FXML
    private Text timer;
    
    private void timestop() {
    	timeline.stop();
    }

    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1),
                    e -> {
                        if(time.getCurrentTime().equals("9 ")){// 공백을 포함해야 멈춤
                            timestop();
                        }
                        time.oneSecondPassed();
                        timer.setText(time.getCurrentTime());
            }));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timer.setFont(FontUtils.loadFontFromResource("fonts/NanumSquareNeo-cBd.ttf", 20));
        timer.setText(time.getCurrentTime());

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}

