import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class BattlesweeperController implements Initializable {

    @FXML
    public GridPane root;

    @FXML
    public ImageView image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image img = new Image("http://icons.iconarchive.com/icons/iconka/meow-2/64/cat-rascal-icon.png");
        image.setImage(img);
    }
}