package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.*;

public class TeamDashboardController {

    @FXML private Label teamNameLbl;
    @FXML private TextField nameTf;
    @FXML private Button signBtn;
    @FXML private Button unsignBtn;
    @FXML private TableView<Player> playerTv;
    @FXML private TableColumn<Player, String> nameCol;
    @FXML private TableColumn<Player, String> posCol;
    @FXML private FlowPane activePane;

    private final ObservableList<Player> roster = FXCollections.observableArrayList();

    private Image noneImage;
    private Image teamJerseyImage;

    private final List<ImageView> slots = new ArrayList<>();
    private final Map<ImageView, Player> slotOwner = new HashMap<>();

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(d -> d.getValue().nameProperty());
        posCol.setCellValueFactory(d -> d.getValue().positionProperty());
        playerTv.setItems(roster);
        signBtn.disableProperty().bind(nameTf.textProperty().isEmpty());
        unsignBtn.disableProperty().bind(playerTv.getSelectionModel().selectedItemProperty().isNull());
    }

    public void init(String teamName, String jerseyPng) {
        teamNameLbl.setText(teamName);
        noneImage = new Image(getClass().getResourceAsStream("/view/image/none.png"));
        teamJerseyImage = new Image(getClass().getResourceAsStream("/view/image/" + jerseyPng));
        roster.setAll(sampleRosterFor(teamName));
        populateActiveIcons(6);
    }

    @FXML
    private void sign() {
        String name = nameTf.getText().trim();
        if (name.isEmpty()) return;
        roster.add(new Player(name, "Forward"));
        nameTf.clear();
    }

    @FXML
    private void unsign() {
        Player p = playerTv.getSelectionModel().getSelectedItem();
        if (p == null) return;
        playerTv.getItems().remove(p);
        for (ImageView iv : new ArrayList<>(slotOwner.keySet())) {
            if (p.equals(slotOwner.get(iv))) {
                slotOwner.remove(iv);
                iv.setImage(noneImage);
            }
        }
    }

    @FXML
    private void close() {
        Stage s = (Stage) teamNameLbl.getScene().getWindow();
        s.close();
    }

    private void populateActiveIcons(int count) {
        activePane.getChildren().clear();
        slots.clear();
        slotOwner.clear();

        activePane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        activePane.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        javafx.scene.layout.HBox row1 = new javafx.scene.layout.HBox(16);
        row1.setAlignment(javafx.geometry.Pos.CENTER);
        javafx.scene.layout.HBox row2 = new javafx.scene.layout.HBox(16);
        row2.setAlignment(javafx.geometry.Pos.CENTER);
        javafx.scene.layout.HBox row3 = new javafx.scene.layout.HBox(16);
        row3.setAlignment(javafx.geometry.Pos.CENTER);

        activePane.getChildren().addAll(row1, row2, row3);

        row1.getChildren().add(createSlot());
        row2.getChildren().add(createSlot());
        row2.getChildren().add(createSlot());
        row2.getChildren().add(createSlot());
        row3.getChildren().add(createSlot());
    }

    private ImageView createSlot() {
        ImageView iv = new ImageView(noneImage);
        iv.setFitWidth(64);
        iv.setFitHeight(64);
        iv.setPreserveRatio(true);
        iv.setOnMouseClicked(e -> assignToSlot(iv));
        slots.add(iv);
        return iv;
    }

    private void assignToSlot(ImageView slot) {
        Player sel = playerTv.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        slot.setImage(teamJerseyImage);
        slotOwner.put(slot, sel);
    }

    private ObservableList<Player> sampleRosterFor(String team) {
        if ("Haymarket Storm".equals(team)) {
            return FXCollections.observableArrayList(
                new Player("Nathan Gasnier","Fullback"),
                new Player("Finn Gallen","Wing"),
                new Player("Jake Civoniceva","Centre"),
                new Player("Connor Watmough","Forward"),
                new Player("Daniel Kennedy","Halfback"),
                new Player("Aiden Meninga","Wing"),
                new Player("Xavier Webcke","Forward")
            );
        } else if ("Chippendale Panthers".equals(team)) {
            return FXCollections.observableArrayList(
                new Player("Harvey Sutton","Fullback"),
                new Player("Riley McMahon","Wing"),
                new Player("Mason Grant","Centre"),
                new Player("Oscar Talbot","Forward"),
                new Player("Jasper Reid","Halfback"),
                new Player("Levi Coleman","Wing"),
                new Player("Patrick Hayes","Forward")
            );
        } else if ("Broadway Bulldogs".equals(team)) {
            return FXCollections.observableArrayList(
                new Player("Ethan Johns","Fullback"),
                new Player("Blake Rogers","Halfback"),
                new Player("Logan Fraser","Wing"),
                new Player("Hudson Barrett","Centre"),
                new Player("Archie Powell","Forward"),
                new Player("Nate Cooper","Wing"),
                new Player("Kai Armstrong","Forward")
            );
        } else {
            return FXCollections.observableArrayList(
                new Player("Caleb Baker","Fullback"),
                new Player("Ethan Ross","Wing"),
                new Player("Jude Mitchell","Centre"),
                new Player("Ryan Ellis","Forward"),
                new Player("Austin Ward","Halfback"),
                new Player("Miles Carter","Wing"),
                new Player("Zac Kelly","Forward")
            );
        }
    }

    public static class Player {
        private final SimpleStringProperty name = new SimpleStringProperty();
        private final SimpleStringProperty position = new SimpleStringProperty();
        public Player(String name, String position) { this.name.set(name); this.position.set(position); }
        public String getName() { return name.get(); }
        public String getPosition() { return position.get(); }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty positionProperty() { return position; }
    }
}


