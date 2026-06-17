package controller;

import au.edu.uts.ap.javafx.Controller;
import au.edu.uts.ap.javafx.ViewLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.application.League;
import model.application.Manager;
import model.application.Team;

import java.io.InputStream;
import java.util.Locale;

public class ManagerDashboardController extends Controller<League> {
    @FXML private Label teamNameLbl;
    @FXML private ImageView jerseyIv;
    @FXML private Button withdrawBtn;
    @FXML private Button manageBtn;
    @FXML private Button swapBtn;

    @FXML
    private void initialize() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        Manager manager = model.getLoggedInManager();
        Team team = manager == null ? null : manager.getTeam();

        teamNameLbl.setText(team == null ? "No Team" : team.toString());
        jerseyIv.setImage(loadImage(team == null ? "none.png" : jerseyFile(team)));
        withdrawBtn.setDisable(team == null);
        manageBtn.setDisable(team == null);
        swapBtn.setDisable(!hasSwapOptions());
    }

    private String jerseyFile(Team team) {
        return team.getTeamName().toLowerCase(Locale.ENGLISH) + ".png";
    }

    private Image loadImage(String fileName) {
        InputStream stream = getClass().getResourceAsStream("/view/image/" + fileName);
        if (stream == null) {
            stream = getClass().getResourceAsStream("/view/image/none.png");
        }
        if (stream == null) {
            throw new IllegalStateException("Missing jersey image: " + fileName);
        }
        return new Image(stream);
    }

    private boolean hasSwapOptions() {
        return !model.getManageableTeams().getTeams().isEmpty()
                || !model.getAddableTeamTemplates().getTeams().isEmpty();
    }

    @FXML
    private void withdraw() {
        Manager manager = model.getLoggedInManager();
        if (manager != null && manager.getTeam() != null) {
            model.withdrawManagerFromTeam(manager);
            refreshDashboard();
        }
    }

    @FXML
    private void closeWindow() {
        stage.close();
    }

    @FXML
    private void openTeamDashboard() {
        Manager manager = model.getLoggedInManager();
        if (manager == null || manager.getTeam() == null) {
            return;
        }

        Stage dashboardStage = new Stage();
        dashboardStage.initOwner(stage);
        dashboardStage.initModality(Modality.WINDOW_MODAL);
        ViewLoader.showStage(model, "/view/TeamDashboardView.fxml", "Team Dashboard", dashboardStage);
    }

    @FXML
    private void openSwap() {
        if (!hasSwapOptions()) {
            return;
        }

        Stage swapStage = new Stage();
        swapStage.initOwner(stage);
        swapStage.initModality(Modality.WINDOW_MODAL);
        ViewLoader.showStage(model, "/view/SwapView.fxml", "Swap Team", swapStage,
                this::refreshDashboard);
    }
}
