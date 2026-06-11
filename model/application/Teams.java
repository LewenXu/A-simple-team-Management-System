package model.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class Teams {
    private final ObservableList<Team> teams;

    public Teams(List<Team> teams) {
        this.teams = FXCollections.observableList(teams);
    }

    public ObservableList<Team> getTeams() {
        return teams;
    }
    public String toString() {
        StringBuilder teamsString = new StringBuilder();
        for (Team team : this.teams) {
            teamsString.append(team.toString()).append("\n");
        }
        return teamsString.toString();
    }
}

