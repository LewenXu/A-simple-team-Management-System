package model.application;

import java.util.ArrayList;
import java.util.List;
import model.exception.FillException;
import model.exception.InvalidSigningException;

public class Team {
    public static int REQUIRED_TEAM_SIZE = 5;
    private final String localName;
    private final String teamName;
    private Manager manager;
    private final Players allPlayers;
    private final Player[] currentTeam;
    public Team(String localName, String teamName, Manager manager, Players allPlayers) {
        this.localName = localName;
        this.teamName = teamName;
        this.manager = manager;
        this.allPlayers = allPlayers;
        this.currentTeam = new Player[REQUIRED_TEAM_SIZE];
    }
    public String getTeamName() { return this.teamName; }
    public String getLocalName() { return this.localName; }
    public Manager getManager() { return this.manager; }
    public Players getAllPlayers() { return this.allPlayers; }
    public void setManager(Manager manager) { this.manager = manager; }
    public String toString() { return this.localName + " " + this.teamName; }
    public List<Player> getCurrentTeamAsList() {
        List<Player> out = new ArrayList<Player>();
        for (Player p : currentTeam) if (p != null) out.add(p);
        return out;
    }
    public Player getAt(int index) {
        ensureIndexBounds(index);
        return currentTeam[index];
    }
    public int indexOf(Player p) {
        if (p == null) return -1;
        for (int i = 0; i < currentTeam.length; i++) if (p == currentTeam[i]) return i;
        return -1;
    }
    public void addToCurrent(Player p) throws InvalidSigningException {
        if (p == null) throw new IllegalArgumentException("player is null");
        if (indexOf(p) >= 0) throw new InvalidSigningException("Player already in the current team.");
        ensureAvailablePlayer(p);
        if (currentSize() >= REQUIRED_TEAM_SIZE) throw new InvalidSigningException("Current team is full.");
        int slot = firstEmptySlot();
        if (slot < 0) throw new InvalidSigningException("No empty slot in current team.");
        currentTeam[slot] = p;
        tryRemoveFromPool(p);
    }
    public void addToIndex(int index, Player p) throws InvalidSigningException {
        ensureIndexBounds(index);
        if (p == null) throw new IllegalArgumentException("player is null");
        if (indexOf(p) >= 0) throw new InvalidSigningException("Player already in the current team.");
        ensureAvailablePlayer(p);
        if (currentTeam[index] != null) throw new InvalidSigningException("Target position is already allocated.");
        if (currentSize() >= REQUIRED_TEAM_SIZE) throw new InvalidSigningException("Current team is full.");
        currentTeam[index] = p;
        tryRemoveFromPool(p);
    }
    public void removeFromCurrent(Player p) {
        if (p == null) return;
        int idx = indexOf(p);
        if (idx >= 0) {
            currentTeam[idx] = null;
            tryAddBackToPool(p);
        }
    }
    public void removeFromCurrent1(Player p) { removeFromCurrent(p); }
    public void signPlayer(Player p) throws InvalidSigningException {
        if (p == null) throw new IllegalArgumentException("player is null");
        if (p.getTeam() != null) {
            throw new InvalidSigningException("Player is already signed to a team.");
        }
        if (allPlayers.getPlayers().contains(p) || indexOf(p) >= 0) {
            throw new InvalidSigningException("Player is already signed to this team.");
        }
        p.setTeam(this);
        allPlayers.add(p);
    }
    public void unsignPlayer(Player p) {
        if (p == null || p.getTeam() != this) return;
        int index = indexOf(p);
        if (index >= 0) currentTeam[index] = null;
        allPlayers.remove(p);
        p.setTeam(null);
    }
    public void fillToRequired() throws FillException {
        while (currentSize() < REQUIRED_TEAM_SIZE) {
            Player candidate = firstCandidateFromPoolNotInCurrent();
            if (candidate == null)
                throw new FillException("Not enough players to fill to " + REQUIRED_TEAM_SIZE + ".");
            try { addToCurrent(candidate); }
            catch (InvalidSigningException e) { throw new FillException(e.getMessage()); }
        }
    }
    public void clearCurrent() {
        for (int i = 0; i < currentTeam.length; i++) {
            Player p = currentTeam[i];
            if (p != null) {
                tryAddBackToPool(p);
                currentTeam[i] = null;
            }
        }
    }
    private void ensureIndexBounds(int index) {
        if (index < 0 || index >= REQUIRED_TEAM_SIZE)
            throw new IllegalArgumentException("index out of range: " + index);
    }

    private int currentSize() {
        int c = 0;
        for (Player p : currentTeam) if (p != null) c++;
        return c;
    }
    private void ensureAvailablePlayer(Player p) throws InvalidSigningException {
        if (!allPlayers.getPlayers().contains(p)) {
            throw new InvalidSigningException("Player is not available for this team.");
        }
        if (p.getTeam() != null && p.getTeam() != this) {
            throw new InvalidSigningException("Player is signed to another team.");
        }
        if (p.getTeam() == null) p.setTeam(this);
    }
    private int firstEmptySlot() {
        for (int i = 0; i < currentTeam.length; i++) if (currentTeam[i] == null) return i;
        return -1;
    }
    private Player firstCandidateFromPoolNotInCurrent() {
        if (allPlayers == null || allPlayers.getPlayers() == null) return null;
        for (Player q : allPlayers.getPlayers()) if (indexOf(q) < 0) return q;
        return null;
    }
    private void tryRemoveFromPool(Player p) {
        if (allPlayers != null && allPlayers.getPlayers() != null) {
            allPlayers.getPlayers().remove(p);
        }
    }
    private void tryAddBackToPool(Player p) {
        if (allPlayers != null && allPlayers.getPlayers() != null) {
            if (!allPlayers.getPlayers().contains(p)) allPlayers.getPlayers().add(p);
        }
    }
}
