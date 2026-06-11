package controller;

import au.edu.uts.ap.javafx.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ManagerDashboardController extends Controller {

    @FXML private Label teamNameLbl;
    @FXML private ImageView jerseyIv;

    private String currentTeam = "Ultimo Eels";

    private final Map<String, String> jerseyFile = new HashMap<String, String>() {{
        put("Ultimo Eels", "eels.png");
        put("Chippendale Panthers", "panthers.png");
        put("Haymarket Storm", "storm.png");
        put("Broadway Bulldogs", "bulldogs.png");
    }};

    @FXML
    private void initialize() { setTeam(currentTeam); }

    public void setModel(Object model) { setTeam(currentTeam); }

    private void setTeam(String name) {
        currentTeam = name;
        teamNameLbl.setText(name);
        String file = jerseyFile.getOrDefault(name, "eels.png");
        InputStream is = getClass().getResourceAsStream("/view/image/" + file);
        if (is == null) is = getClass().getResourceAsStream("/view/image/eels.png");
        jerseyIv.setImage(new Image(is));
    }

    @FXML private void withdraw() {}
    @FXML private void closeWindow() {
        Stage stage = (Stage) teamNameLbl.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void openTeamDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TeamDashboardView.fxml"));
            Parent root = loader.load();
            TeamDashboardController c = loader.getController();
            String jerseyPng = jerseyFile.getOrDefault(currentTeam, "eels.png");
            c.init(currentTeam, jerseyPng);
            Stage stage = new Stage();
            stage.setTitle("Team Dashboard");
            stage.initOwner(teamNameLbl.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSwap() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SwapView.fxml"));
            Parent root = loader.load();
            SwapController sc = loader.getController();
            sc.init(currentTeam);
            Stage stage = new Stage();
            stage.setTitle("Swap");
            stage.initOwner(teamNameLbl.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            String chosen = sc.getChosenTeam();
            if (chosen != null && !chosen.equals(currentTeam)) setTeam(chosen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


