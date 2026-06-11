package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class SwapController {

    @FXML private ListView<String> teamLv;
    @FXML private Button swapBtn;

    private String chosenTeam;

    @FXML
    private void initialize() {
        teamLv.setItems(FXCollections.observableArrayList(
            "Chippendale Panthers",
            "Haymarket Storm",
            "Broadway Bulldogs",
            "Ultimo Eels"
        ));
        swapBtn.disableProperty().bind(teamLv.getSelectionModel().selectedItemProperty().isNull());
    }

    public void init(String currentTeam) {
        int i = teamLv.getItems().indexOf(currentTeam);
        if (i >= 0) teamLv.getSelectionModel().select(i);
    }

    public String getChosenTeam() {
        return chosenTeam;
    }

    @FXML
    private void swap() {
        chosenTeam = teamLv.getSelectionModel().getSelectedItem();
        close();
    }

    @FXML
    private void close() {
        Stage stage = (Stage) teamLv.getScene().getWindow();
        stage.close();
    }
}

