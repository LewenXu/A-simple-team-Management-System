package model.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Players {
    private final ObservableList<Player> players;

    public Players(List<Player> players) {
        this.players = FXCollections.observableList(players);
    }

    public ObservableList<Player> getPlayers() {
        return players;
    }

    public void add(Player player) {
        this.players.add(player);
    }

    public void remove(Player player) {
        this.players.remove(player);
    }
   
    public Player player(String name) {
        for (Player player : players) {
            if (player.getFullName().equals(name)) {
                return player;
            }
        }
        return null;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Player player : players) {
            sb.append(player.getFullName()).append("\n");
        }
        return sb.toString();
    }
}

