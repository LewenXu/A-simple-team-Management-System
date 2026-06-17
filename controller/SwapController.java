package controller;

import au.edu.uts.ap.javafx.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.application.League;
import model.application.Manager;
import model.application.Team;

import java.io.InputStream;
import java.util.Locale;

public class SwapController extends Controller<League> {
    @FXML private ListView<Team> teamLv;
    @FXML private Button swapBtn;
    @FXML private ComboBox<Team> addTeamCb;
    @FXML private ImageView addTeamJerseyIv;
    @FXML private Button addTeamBtn;

    private Image noneImage;

    @FXML
    private void initialize() {
        noneImage = loadImage("none.png");
        teamLv.setItems(model.getManageableTeams().getTeams());
        swapBtn.disableProperty().bind(
                teamLv.getSelectionModel().selectedItemProperty().isNull());
        addTeamBtn.disableProperty().bind(
                addTeamCb.getSelectionModel().selectedItemProperty().isNull());
        addTeamCb.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldTeam, newTeam) -> updateAddTeamJersey(newTeam));
        refreshAddableTeams();
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
    private void addTeam() {
        Team template = addTeamCb.getSelectionModel().getSelectedItem();
        if (template == null) {
            return;
        }

        Team addedTeam = model.addManageableTeamFromTemplate(template);
        refreshAddableTeams();
        teamLv.getSelectionModel().select(addedTeam);
        int index = teamLv.getItems().indexOf(addedTeam);
        if (index >= 0) {
            teamLv.scrollTo(index);
        }
    }

    @FXML
    private void close() {
        stage.close();
    }

    private void refreshAddableTeams() {
        addTeamCb.setItems(model.getAddableTeamTemplates().getTeams());
        if (addTeamCb.getItems().isEmpty()) {
            addTeamCb.getSelectionModel().clearSelection();
            updateAddTeamJersey(null);
        } else {
            addTeamCb.getSelectionModel().selectFirst();
        }
    }

    private void updateAddTeamJersey(Team team) {
        addTeamJerseyIv.setImage(team == null
                ? noneImage
                : loadImage(team.getTeamName().toLowerCase(Locale.ENGLISH) + ".png"));
    }

    private Image loadImage(String fileName) {
        InputStream stream = getClass().getResourceAsStream("/view/image/" + fileName);
        if (stream == null) {
            stream = getClass().getResourceAsStream("/view/image/none.png");
        }
        if (stream == null) {
            throw new IllegalStateException("Missing image: " + fileName);
        }
        return new Image(stream);
    }
}
