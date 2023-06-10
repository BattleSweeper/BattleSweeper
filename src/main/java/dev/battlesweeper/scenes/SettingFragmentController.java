package dev.battlesweeper.scenes;
import dev.battlesweeper.utils.ResourceUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
public class SettingFragmentController implements Initializable, FragmentUpdater {

    @FXML
    private Button buttonResolution1;
    @FXML
    private Button buttonResolution2;
    @FXML
    private Slider sliderVolume;
    @FXML
    private Text textVolume;

    @FXML
    private Button buttonPrev;

    private HomeController parentController;

    @Override
    public void setParent(Initializable parent) {
        this.parentController = (HomeController) parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        buttonPrev.setOnAction(event -> {
            try {
                parentController.setFragment(ResourceUtils.getResource("PlayFragment.fxml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        buttonResolution1.setOnAction(event -> {

        });
        buttonResolution2.setOnAction(event -> {

        });



    }

    public void updateVolume(MouseEvent event){
        textVolume.setText(String.valueOf((int)sliderVolume.getValue())+"%");

    }


}
