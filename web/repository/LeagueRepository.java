package web.repository;

import java.util.List;

import web.domain.ManagerAccount;
import web.domain.PlayerProfile;
import web.domain.TeamSummary;

public interface LeagueRepository {
    void initialize();

    ManagerAccount findManagerById(int id);

    ManagerAccount refreshManager(int id);

    TeamSummary findTeamById(int teamId);

    TeamSummary findTeamForManager(int managerId);

    List<TeamSummary> findAllTeams();

    List<TeamSummary> findAvailableTeamsForManager(int managerId);

    List<PlayerProfile> findPlayersForTeam(int teamId);

    List<PlayerProfile> searchPlayersForTeam(int teamId, String query);

    int createPlayer(int managerId, String firstName, String lastName, String position);

    void unsignPlayer(int managerId, int playerId);

    void assignManagerToTeam(int managerId, int teamId);

    void withdrawManager(int managerId);

    void close();
}
