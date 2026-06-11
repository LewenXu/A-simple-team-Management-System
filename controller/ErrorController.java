package controller;

import au.edu.uts.ap.javafx.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ErrorController extends Controller<String> {
    @FXML private ImageView bannerIv;
    @FXML private Label titleLbl;
    @FXML private Label messageLbl;
    @FXML private void initialize() {
        bannerIv.setImage(new Image(getClass().getResourceAsStream("/view/image/error.png")));
        titleLbl.setText("UnauthorisedAccessException");
        messageLbl.setText(model == null ? "" : model);
    }

    @FXML private void ok() { if (stage != null) stage.close(); }
}
