package controller;

import au.edu.uts.ap.javafx.Controller;
import au.edu.uts.ap.javafx.ViewLoader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.application.League;
import model.application.Player;
import model.application.Team;
import model.enums.Position;
import model.exception.InvalidSigningException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TeamDashboardController extends Controller<League> {
    @FXML private Label teamNameLbl;
    @FXML private TextField nameTf;
    @FXML private ComboBox<Position> positionCb;
    @FXML private Button signBtn;
    @FXML private Button unsignBtn;
    @FXML private TableView<Player> playerTv;
    @FXML private TableColumn<Player, String> nameCol;
    @FXML private TableColumn<Player, String> posCol;
    @FXML private FlowPane activePane;

    private final List<ImageView> slots = new ArrayList<>();
    private Team team;
    private Image noneImage;
    private Image teamJerseyImage;
    private int selectedActiveIndex = -1;

    @FXML
    private void initialize() {
        if (model.getLoggedInManager() == null || model.getLoggedInManager().getTeam() == null) {
            throw new IllegalStateException("A manager with a team is required");
        }

        team = model.getLoggedInManager().getTeam();
        teamNameLbl.setText(team.toString());
        noneImage = loadImage("none.png");
        teamJerseyImage = loadImage(
                team.getTeamName().toLowerCase(Locale.ENGLISH) + ".png");

        nameCol.setCellValueFactory(data -> data.getValue().fullNameProperty());
        posCol.setCellValueFactory(data -> data.getValue().positionProperty());
        playerTv.setItems(team.getAllPlayers().getPlayers());
        playerTv.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldPlayer, newPlayer) -> {
                    if (newPlayer != null) {
                        selectedActiveIndex = -1;
                        refreshSlotSelection();
                    }
                    updateUnsignButton();
                });

        positionCb.setItems(FXCollections.observableArrayList(Position.values()));
        positionCb.setValue(Position.Forward);
        signBtn.disableProperty().bind(
                nameTf.textProperty().isEmpty().or(positionCb.valueProperty().isNull()));

        createActiveSlots();
        refreshActiveTeam();
    }

    @FXML
    private void sign() {
        String fullName = nameTf.getText().trim().replaceAll("\\s+", " ");
        int separator = fullName.indexOf(' ');
        if (separator <= 0 || separator == fullName.length() - 1) {
            showError("Enter both a first name and a last name.");
            return;
        }

        try {
            Player player = findPlayer(fullName);
            if (player == null) {
                player = new Player(
                        fullName.substring(0, separator),
                        fullName.substring(separator + 1),
                        null,
                        positionCb.getValue());
                team.signPlayer(player);
                model.getPlayers().add(player);
            } else {
                team.signPlayer(player);
            }
            nameTf.clear();
            playerTv.getSelectionModel().select(player);
        } catch (InvalidSigningException e) {
            showError(e.getMessage());
        }
    }

    private Player findPlayer(String fullName) {
        for (Player player : model.getPlayers().getPlayers()) {
            if (player.getFullName().equalsIgnoreCase(fullName)) {
                return player;
            }
        }
        return null;
    }

    @FXML
    private void unsign() {
        Player player = selectedActiveIndex >= 0
                ? team.getAt(selectedActiveIndex)
                : playerTv.getSelectionModel().getSelectedItem();
        if (player == null) {
            return;
        }

        team.unsignPlayer(player);
        selectedActiveIndex = -1;
        playerTv.getSelectionModel().clearSelection();
        refreshActiveTeam();
    }

    @FXML
    private void close() {
        stage.close();
    }

    private void createActiveSlots() {
        activePane.getChildren().clear();
        slots.clear();
        activePane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        activePane.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        HBox row1 = createRow();
        HBox row2 = createRow();
        HBox row3 = createRow();
        activePane.getChildren().addAll(row1, row2, row3);

        row1.getChildren().add(createSlot(0));
        row2.getChildren().addAll(createSlot(1), createSlot(2), createSlot(3));
        row3.getChildren().add(createSlot(4));
    }

    private HBox createRow() {
        HBox row = new HBox(16);
        row.setAlignment(javafx.geometry.Pos.CENTER);
        return row;
    }

    private ImageView createSlot(int index) {
        ImageView imageView = new ImageView(noneImage);
        imageView.setFitWidth(64);
        imageView.setFitHeight(64);
        imageView.setPreserveRatio(true);
        imageView.setOnMouseClicked(event -> handleSlot(index));
        slots.add(imageView);
        return imageView;
    }

    private void handleSlot(int index) {
        Player activePlayer = team.getAt(index);
        if (activePlayer != null) {
            selectedActiveIndex = index;
            playerTv.getSelectionModel().clearSelection();
            refreshSlotSelection();
            updateUnsignButton();
            return;
        }

        Player selectedPlayer = playerTv.getSelectionModel().getSelectedItem();
        if (selectedPlayer == null) {
            return;
        }

        try {
            team.addToIndex(index, selectedPlayer);
            selectedActiveIndex = index;
            refreshActiveTeam();
        } catch (InvalidSigningException e) {
            showError(e.getMessage());
        }
    }

    private void refreshActiveTeam() {
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setImage(team.getAt(i) == null ? noneImage : teamJerseyImage);
        }
        refreshSlotSelection();
        updateUnsignButton();
    }

    private void refreshSlotSelection() {
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setStyle(i == selectedActiveIndex
                    ? "-fx-effect: dropshadow(gaussian, #2f80ed, 10, 0.6, 0, 0);"
                    : "");
        }
    }

    private void updateUnsignButton() {
        boolean activeSelected = selectedActiveIndex >= 0
                && team.getAt(selectedActiveIndex) != null;
        unsignBtn.setDisable(!activeSelected
                && playerTv.getSelectionModel().getSelectedItem() == null);
    }

    private Image loadImage(String fileName) {
        InputStream stream = getClass().getResourceAsStream("/view/image/" + fileName);
        if (stream == null) {
            throw new IllegalStateException("Missing image: " + fileName);
        }
        return new Image(stream);
    }

    private void showError(String message) {
        ViewLoader.showStage(message, "/view/ErrorView.fxml", "Error", new Stage());
    }
}
