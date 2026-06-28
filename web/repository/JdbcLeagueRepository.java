package web.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import web.domain.ManagerAccount;
import web.domain.PlayerProfile;
import web.domain.TeamSummary;

public class JdbcLeagueRepository implements LeagueRepository {
    private final ConnectionFactory connectionFactory;

    public JdbcLeagueRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void initialize() {
        try (Connection connection = connectionFactory.openConnection()) {
            createSchemaIfNeeded(connection);
            if (countRows(connection, "teams") == 0) {
                seed(connection);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not initialise JDBC persistence.", e);
        }
    }

    @Override
    public ManagerAccount findManagerById(int id) {
        String sql = "SELECT id, first_name, last_name, team_id FROM managers WHERE id = ?";
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return toManager(result);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not find manager.", e);
        }
    }

    @Override
    public ManagerAccount refreshManager(int id) {
        ManagerAccount account = findManagerById(id);
        if (account == null) {
            throw new RepositoryException("Manager account was not found.");
        }
        return account;
    }

    @Override
    public TeamSummary findTeamById(int teamId) {
        String sql = "SELECT t.id, t.local_name, t.team_name, m.id AS manager_id "
                + "FROM teams t LEFT JOIN managers m ON m.team_id = t.id WHERE t.id = ?";
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return toTeam(result);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not find team.", e);
        }
    }

    @Override
    public TeamSummary findTeamForManager(int managerId) {
        String sql = "SELECT t.id, t.local_name, t.team_name, m.id AS manager_id "
                + "FROM managers m JOIN teams t ON t.id = m.team_id WHERE m.id = ?";
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, managerId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return toTeam(result);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not find managed team.", e);
        }
    }

    @Override
    public List<TeamSummary> findAllTeams() {
        String sql = "SELECT t.id, t.local_name, t.team_name, m.id AS manager_id "
                + "FROM teams t LEFT JOIN managers m ON m.team_id = t.id ORDER BY t.local_name, t.team_name";
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            List<TeamSummary> teams = new ArrayList<TeamSummary>();
            while (result.next()) {
                teams.add(toTeam(result));
            }
            return teams;
        } catch (SQLException e) {
            throw new RepositoryException("Could not list teams.", e);
        }
    }

    @Override
    public List<TeamSummary> findAvailableTeamsForManager(int managerId) {
        refreshManager(managerId);
        String sql = "SELECT t.id, t.local_name, t.team_name, m.id AS manager_id "
                + "FROM teams t LEFT JOIN managers m ON m.team_id = t.id "
                + "WHERE m.id IS NULL ORDER BY t.local_name, t.team_name";
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {
            List<TeamSummary> teams = new ArrayList<TeamSummary>();
            while (result.next()) {
                teams.add(toTeam(result));
            }
            return teams;
        } catch (SQLException e) {
            throw new RepositoryException("Could not list available teams.", e);
        }
    }

    @Override
    public List<PlayerProfile> findPlayersForTeam(int teamId) {
        String sql = "SELECT id, first_name, last_name, playing_position, team_id "
                + "FROM players WHERE team_id = ? ORDER BY last_name, first_name";
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                List<PlayerProfile> players = new ArrayList<PlayerProfile>();
                while (result.next()) {
                    players.add(toPlayer(result));
                }
                return players;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not list players.", e);
        }
    }

    @Override
    public List<PlayerProfile> searchPlayersForTeam(int teamId, String query) {
        String needle = query == null ? "" : query.trim().toLowerCase();
        List<PlayerProfile> matches = new ArrayList<PlayerProfile>();
        for (PlayerProfile player : findPlayersForTeam(teamId)) {
            String haystack = (player.getFullName() + " " + player.getPosition()).toLowerCase();
            if (needle.isEmpty() || haystack.contains(needle)) {
                matches.add(player);
            }
        }
        return matches;
    }

    @Override
    public int createPlayer(int managerId, String firstName, String lastName, String position) {
        validateRequired(firstName, "First name");
        validateRequired(lastName, "Last name");
        validateRequired(position, "Position");

        Connection connection = null;
        try {
            connection = connectionFactory.openConnection();
            connection.setAutoCommit(false);
            int teamId = requireManagerTeamId(connection, managerId);
            int playerId = nextPlayerId(connection);
            String sql = "INSERT INTO players (id, first_name, last_name, playing_position, team_id) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, playerId);
                statement.setString(2, firstName.trim());
                statement.setString(3, lastName.trim());
                statement.setString(4, position.trim());
                statement.setInt(5, teamId);
                statement.executeUpdate();
            }
            connection.commit();
            return playerId;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw new RepositoryException("Could not sign player.", e);
        } finally {
            closeQuietly(connection);
        }
    }

    @Override
    public void unsignPlayer(int managerId, int playerId) {
        Connection connection = null;
        try {
            connection = connectionFactory.openConnection();
            connection.setAutoCommit(false);
            int teamId = requireManagerTeamId(connection, managerId);
            if (!playerBelongsToTeam(connection, playerId, teamId)) {
                throw new RepositoryException("The selected player is not signed to your team.");
            }
            try (PreparedStatement active = connection.prepareStatement("DELETE FROM active_slots WHERE player_id = ?")) {
                active.setInt(1, playerId);
                active.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("UPDATE players SET team_id = NULL WHERE id = ?")) {
                statement.setInt(1, playerId);
                statement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw new RepositoryException("Could not unsign player.", e);
        } finally {
            closeQuietly(connection);
        }
    }

    @Override
    public void assignManagerToTeam(int managerId, int teamId) {
        Connection connection = null;
        try {
            connection = connectionFactory.openConnection();
            connection.setAutoCommit(false);
            requireManager(connection, managerId);
            requireTeam(connection, teamId);
            Integer currentOwner = managerIdForTeam(connection, teamId);
            if (currentOwner != null && currentOwner.intValue() != managerId) {
                throw new RepositoryException("That team already has a manager.");
            }
            try (PreparedStatement statement = connection.prepareStatement("UPDATE managers SET team_id = ? WHERE id = ?")) {
                statement.setInt(1, teamId);
                statement.setInt(2, managerId);
                statement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw new RepositoryException("Could not assign team.", e);
        } finally {
            closeQuietly(connection);
        }
    }

    @Override
    public void withdrawManager(int managerId) {
        String sql = "UPDATE managers SET team_id = NULL WHERE id = ?";
        try (Connection connection = connectionFactory.openConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, managerId);
            if (statement.executeUpdate() == 0) {
                throw new RepositoryException("Manager account was not found.");
            }
        } catch (SQLException e) {
            throw new RepositoryException("Could not withdraw from team.", e);
        }
    }

    @Override
    public void close() {
    }

    private void createSchemaIfNeeded(Connection connection) throws SQLException {
        if (!tableExists(connection, "teams")) {
            execute(connection, "CREATE TABLE teams ("
                    + "id INTEGER NOT NULL PRIMARY KEY, "
                    + "local_name VARCHAR(80) NOT NULL, "
                    + "team_name VARCHAR(80) NOT NULL)");
        }
        if (!tableExists(connection, "managers")) {
            execute(connection, "CREATE TABLE managers ("
                    + "id INTEGER NOT NULL PRIMARY KEY, "
                    + "first_name VARCHAR(80) NOT NULL, "
                    + "last_name VARCHAR(80) NOT NULL, "
                    + "team_id INTEGER)");
        }
        if (!tableExists(connection, "players")) {
            execute(connection, "CREATE TABLE players ("
                    + "id INTEGER NOT NULL PRIMARY KEY, "
                    + "first_name VARCHAR(80) NOT NULL, "
                    + "last_name VARCHAR(80) NOT NULL, "
                    + "playing_position VARCHAR(30) NOT NULL, "
                    + "team_id INTEGER)");
        }
        if (!tableExists(connection, "active_slots")) {
            execute(connection, "CREATE TABLE active_slots ("
                    + "team_id INTEGER NOT NULL, "
                    + "slot_number INTEGER NOT NULL, "
                    + "player_id INTEGER, "
                    + "PRIMARY KEY (team_id, slot_number))");
        }
    }

    private void seed(Connection connection) throws SQLException {
        for (Object[] row : SeedFixtures.TEAMS) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO teams (id, local_name, team_name) VALUES (?, ?, ?)")) {
                statement.setInt(1, (Integer) row[0]);
                statement.setString(2, (String) row[1]);
                statement.setString(3, (String) row[2]);
                statement.executeUpdate();
            }
        }
        for (Object[] row : SeedFixtures.MANAGERS) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO managers (id, first_name, last_name, team_id) VALUES (?, ?, ?, ?)")) {
                statement.setInt(1, (Integer) row[0]);
                statement.setString(2, (String) row[1]);
                statement.setString(3, (String) row[2]);
                setNullableInt(statement, 4, (Integer) row[3]);
                statement.executeUpdate();
            }
        }
        for (Object[] row : SeedFixtures.PLAYERS) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO players (id, first_name, last_name, playing_position, team_id) VALUES (?, ?, ?, ?, ?)")) {
                statement.setInt(1, (Integer) row[0]);
                statement.setString(2, (String) row[1]);
                statement.setString(3, (String) row[2]);
                statement.setString(4, (String) row[3]);
                setNullableInt(statement, 5, (Integer) row[4]);
                statement.executeUpdate();
            }
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String[] names = {tableName, tableName.toUpperCase(), tableName.toLowerCase()};
        for (String name : names) {
            try (ResultSet result = metaData.getTables(null, null, name, null)) {
                if (result.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    private int countRows(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            result.next();
            return result.getInt(1);
        }
    }

    private int nextPlayerId(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT MAX(id) FROM players")) {
            result.next();
            return result.getInt(1) + 1;
        }
    }

    private void execute(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private void requireManager(Connection connection, int managerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM managers WHERE id = ?")) {
            statement.setInt(1, managerId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new RepositoryException("Manager account was not found.");
                }
            }
        }
    }

    private void requireTeam(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM teams WHERE id = ?")) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new RepositoryException("Team was not found.");
                }
            }
        }
    }

    private int requireManagerTeamId(Connection connection, int managerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT team_id FROM managers WHERE id = ?")) {
            statement.setInt(1, managerId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new RepositoryException("Manager account was not found.");
                }
                Integer teamId = readNullableInt(result, "team_id");
                if (teamId == null) {
                    throw new RepositoryException("You need to manage a team before editing players.");
                }
                return teamId.intValue();
            }
        }
    }

    private Integer managerIdForTeam(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM managers WHERE team_id = ?")) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return Integer.valueOf(result.getInt("id"));
            }
        }
    }

    private boolean playerBelongsToTeam(Connection connection, int playerId, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM players WHERE id = ? AND team_id = ?")) {
            statement.setInt(1, playerId);
            statement.setInt(2, teamId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private ManagerAccount toManager(ResultSet result) throws SQLException {
        return new ManagerAccount(result.getInt("id"), result.getString("first_name"), result.getString("last_name"),
                readNullableInt(result, "team_id"));
    }

    private TeamSummary toTeam(ResultSet result) throws SQLException {
        return new TeamSummary(result.getInt("id"), result.getString("local_name"), result.getString("team_name"),
                readNullableInt(result, "manager_id"));
    }

    private PlayerProfile toPlayer(ResultSet result) throws SQLException {
        return new PlayerProfile(result.getInt("id"), result.getString("first_name"), result.getString("last_name"),
                result.getString("playing_position"), readNullableInt(result, "team_id"));
    }

    private Integer readNullableInt(ResultSet result, String column) throws SQLException {
        int value = result.getInt(column);
        return result.wasNull() ? null : Integer.valueOf(value);
    }

    private void setNullableInt(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value.intValue());
        }
    }

    private void validateRequired(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new RepositoryException(label + " is required.");
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void closeQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}
