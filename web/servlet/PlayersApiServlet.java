package web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.domain.ManagerAccount;
import web.domain.TeamSummary;
import web.util.JsonResponses;

public class PlayersApiServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ManagerAccount manager = currentManager(request);
        if (manager == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        TeamSummary team = repository().findTeamForManager(manager.getId());
        if (team == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Manager does not have a team.");
            return;
        }
        response.setContentType("application/json;charset=UTF-8");
        JsonResponses.writePlayers(response.getWriter(),
                repository().searchPlayersForTeam(team.getId(), request.getParameter("q")));
    }
}
