package web.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import web.domain.ManagerAccount;
import web.domain.PlayerProfile;
import web.domain.TeamSummary;

public class InMemoryLeagueRepository implements LeagueRepository {
    private final Map<Integer, MutableManager> managers = new LinkedHashMap<Integer, MutableManager>();
    private final Map<Integer, MutableTeam> teams = new LinkedHashMap<Integer, MutableTeam>();
    private final Map<Integer, MutablePlayer> players = new LinkedHashMap<Integer, MutablePlayer>();

    @Override
    public synchronized void initialize() {
        managers.clear();
        teams.clear();
        players.clear();

        for (Object[] row : SeedFixtures.TEAMS) {
            teams.put((Integer) row[0], new MutableTeam((Integer) row[0], (String) row[1], (String) row[2]));
        }
        for (Object[] row : SeedFixtures.MANAGERS) {
            managers.put((Integer) row[0], new MutableManager((Integer) row[0], (String) row[1], (String) row[2],
                    (Integer) row[3]));
        }
        for (Object[] row : SeedFixtures.PLAYERS) {
            players.put((Integer) row[0], new MutablePlayer((Integer) row[0], (String) row[1], (String) row[2],
                    (String) row[3], (Integer) row[4]));
        }
    }

    @Override
    public synchronized ManagerAccount findManagerById(int id) {
        return toAccount(managers.get(id));
    }

    @Override
    public synchronized ManagerAccount refreshManager(int id) {
        return toAccount(requireManager(id));
    }

    @Override
    public synchronized TeamSummary findTeamById(int teamId) {
        return toSummary(teams.get(teamId));
    }

    @Override
    public synchronized TeamSummary findTeamForManager(int managerId) {
        MutableManager manager = requireManager(managerId);
        if (manager.teamId == null) {
            return null;
        }
        return toSummary(teams.get(manager.teamId));
    }

    @Override
    public synchronized List<TeamSummary> findAllTeams() {
        List<TeamSummary> out = new ArrayList<TeamSummary>();
        for (MutableTeam team : teams.values()) {
            out.add(toSummary(team));
        }
        return out;
    }

    @Override
    public synchronized List<TeamSummary> findAvailableTeamsForManager(int managerId) {
        requireManager(managerId);
        List<TeamSummary> out = new ArrayList<TeamSummary>();
        for (MutableTeam team : teams.values()) {
            Integer ownerId = managerIdForTeam(team.id);
            if (ownerId == null) {
                out.add(toSummary(team));
            }
        }
        return out;
    }

    @Override
    public synchronized List<PlayerProfile> findPlayersForTeam(int teamId) {
        requireTeam(teamId);
        List<PlayerProfile> out = new ArrayList<PlayerProfile>();
        for (MutablePlayer player : players.values()) {
            if (player.teamId != null && player.teamId.intValue() == teamId) {
                out.add(toProfile(player));
            }
        }
        sortPlayers(out);
        return out;
    }

    @Override
    public synchronized List<PlayerProfile> searchPlayersForTeam(int teamId, String query) {
        String needle = query == null ? "" : query.trim().toLowerCase();
        List<PlayerProfile> out = new ArrayList<PlayerProfile>();
        for (PlayerProfile player : findPlayersForTeam(teamId)) {
            String haystack = (player.getFullName() + " " + player.getPosition()).toLowerCase();
            if (needle.isEmpty() || haystack.contains(needle)) {
                out.add(player);
            }
        }
        return out;
    }

    @Override
    public synchronized int createPlayer(int managerId, String firstName, String lastName, String position) {
        int teamId = requireManagerTeamId(managerId);
        validateRequired(firstName, "First name");
        validateRequired(lastName, "Last name");
        validateRequired(position, "Position");

        int nextId = 1;
        for (Integer id : players.keySet()) {
            nextId = Math.max(nextId, id.intValue() + 1);
        }
        players.put(nextId, new MutablePlayer(nextId, firstName.trim(), lastName.trim(), position.trim(), teamId));
        return nextId;
    }

    @Override
    public synchronized void unsignPlayer(int managerId, int playerId) {
        int teamId = requireManagerTeamId(managerId);
        MutablePlayer player = players.get(playerId);
        if (player == null || player.teamId == null || player.teamId.intValue() != teamId) {
            throw new RepositoryException("The selected player is not signed to your team.");
        }
        player.teamId = null;
    }

    @Override
    public synchronized void assignManagerToTeam(int managerId, int teamId) {
        MutableManager manager = requireManager(managerId);
        requireTeam(teamId);
        Integer currentOwner = managerIdForTeam(teamId);
        if (currentOwner != null && currentOwner.intValue() != managerId) {
            throw new RepositoryException("That team already has a manager.");
        }
        manager.teamId = teamId;
    }

    @Override
    public synchronized void withdrawManager(int managerId) {
        requireManager(managerId).teamId = null;
    }

    @Override
    public void close() {
    }

    private MutableManager requireManager(int managerId) {
        MutableManager manager = managers.get(managerId);
        if (manager == null) {
            throw new RepositoryException("Manager account was not found.");
        }
        return manager;
    }

    private MutableTeam requireTeam(int teamId) {
        MutableTeam team = teams.get(teamId);
        if (team == null) {
            throw new RepositoryException("Team was not found.");
        }
        return team;
    }

    private int requireManagerTeamId(int managerId) {
        MutableManager manager = requireManager(managerId);
        if (manager.teamId == null) {
            throw new RepositoryException("You need to manage a team before editing players.");
        }
        return manager.teamId.intValue();
    }

    private Integer managerIdForTeam(int teamId) {
        for (MutableManager manager : managers.values()) {
            if (manager.teamId != null && manager.teamId.intValue() == teamId) {
                return Integer.valueOf(manager.id);
            }
        }
        return null;
    }

    private ManagerAccount toAccount(MutableManager manager) {
        if (manager == null) {
            return null;
        }
        return new ManagerAccount(manager.id, manager.firstName, manager.lastName, manager.teamId);
    }

    private TeamSummary toSummary(MutableTeam team) {
        if (team == null) {
            return null;
        }
        return new TeamSummary(team.id, team.localName, team.teamName, managerIdForTeam(team.id));
    }

    private PlayerProfile toProfile(MutablePlayer player) {
        return new PlayerProfile(player.id, player.firstName, player.lastName, player.position, player.teamId);
    }

    private void sortPlayers(List<PlayerProfile> out) {
        Collections.sort(out, new Comparator<PlayerProfile>() {
            @Override
            public int compare(PlayerProfile a, PlayerProfile b) {
                return a.getFullName().compareToIgnoreCase(b.getFullName());
            }
        });
    }

    private void validateRequired(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new RepositoryException(label + " is required.");
        }
    }

    private static class MutableManager {
        private final int id;
        private final String firstName;
        private final String lastName;
        private Integer teamId;

        private MutableManager(int id, String firstName, String lastName, Integer teamId) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.teamId = teamId;
        }
    }

    private static class MutableTeam {
        private final int id;
        private final String localName;
        private final String teamName;

        private MutableTeam(int id, String localName, String teamName) {
            this.id = id;
            this.localName = localName;
            this.teamName = teamName;
        }
    }

    private static class MutablePlayer {
        private final int id;
        private final String firstName;
        private final String lastName;
        private final String position;
        private Integer teamId;

        private MutablePlayer(int id, String firstName, String lastName, String position, Integer teamId) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.position = position;
            this.teamId = teamId;
        }
    }
}
