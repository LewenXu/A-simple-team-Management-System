package controller;

import au.edu.uts.ap.javafx.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import model.application.League;
import model.application.Manager;
import model.application.Team;

public class SwapController extends Controller<League> {
    @FXML private ListView<Team> teamLv;
    @FXML private Button swapBtn;

    @FXML
    private void initialize() {
        teamLv.setItems(model.getManageableTeams().getTeams());
        swapBtn.disableProperty().bind(
                teamLv.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void swap() {
        Team selectedTeam = teamLv.getSelectionModel().getSelectedItem();
        Manager manager = model.getLoggedInManager();
        if (selectedTeam == null || manager == null) {
            return;
        }

        model.setManagerForTeam(manager, selectedTeam);
        stage.close();
    }

    @FXML
    private void close() {
        stage.close();
    }
}
