package model.application;

import model.exception.UnauthorisedAccessException;
import java.util.List;
import java.util.stream.Collectors;

public class League {
    private static League instance;
    private final Teams manageableTeams;
    private final List<Manager> managers;
    private final Players players;
    private Manager loggedInManager;
    private League(Teams seededTeams, Players seededPlayers, List<Manager> seededManagers) {
        this.manageableTeams = new Teams(seededTeams.getTeams().stream().filter(team -> team.getManager() == null).collect(Collectors.toList()));
        this.players = seededPlayers;
        this.managers = seededManagers;
    }
    public static void initialize(Teams seededTeams, Players seededPlayers, List<Manager> seededManagers) {
        if (instance == null) {
            instance = new League(seededTeams, seededPlayers, seededManagers);
        }
        else {
            throw new IllegalStateException("League has already been initialized");
        }
    }
    public static League getInstance() {
        if (instance == null) {
            throw new IllegalStateException("League has not been initialized");
        }
        return instance;
    }
    public Teams getManageableTeams() {
        return manageableTeams;
    }
    public Players getPlayers() { return players; }
    public Manager getLoggedInManager() {
        return loggedInManager;
    }
    public void setLoggedInManager(Manager manager) {
        this.loggedInManager = manager;
    }
    public void setManagerForTeam(Manager manager, Team team){
        if (team == null || manager == null) {
            throw new IllegalArgumentException("Team and Manager cannot be null");
        }
        if (team.getManager() != null) {
            throw new IllegalArgumentException("Team already has a Manager. You should only be calling this method on a Team that is in the manageableTeams list");
        }
        if (manager.getTeam() != null) {
            Team oldTeam = manager.getTeam();
            oldTeam.setManager(null);
            manageableTeams.getTeams().add(oldTeam);
        }
        manager.assignTeam(team);
        team.setManager(manager);
        manageableTeams.getTeams().remove(team);
    }
    public void withdrawManagerfromTeam(Manager manager){
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }
        if (manager.getTeam() == null) {
            throw new IllegalArgumentException("Manager does not have a team to withdraw from");
        }
        manageableTeams.getTeams().add(manager.getTeam());
        manager.getTeam().setManager(null);
        manager.assignTeam(null);
    }
    public Manager validateManager(int id) throws UnauthorisedAccessException {
        for (Manager manager : this.managers) {
            if (manager.hasId(id)) {
                return manager;
            }
        }
        throw new UnauthorisedAccessException("Invalid login credentials");
    }
}

