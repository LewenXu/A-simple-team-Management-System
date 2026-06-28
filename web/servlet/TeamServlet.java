package web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.domain.ManagerAccount;
import web.domain.TeamSummary;
import web.repository.RepositoryException;

public class TeamServlet extends BaseServlet {
    private static final String[] POSITIONS = {"Fullback", "Wing", "Centre", "Halfback", "Forward"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ManagerAccount manager = requireManager(request, response);
        if (manager == null) {
            return;
        }
        TeamSummary team = repository().findTeamForManager(manager.getId());
        if (team == null) {
            setFlash(request, "error", "Choose a team before managing players.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        exposeFlash(request);
        request.setAttribute("manager", manager);
        request.setAttribute("team", team);
        request.setAttribute("players", repository().findPlayersForTeam(team.getId()));
        request.setAttribute("positions", POSITIONS);
        forward(request, response, "team.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ManagerAccount manager = requireManager(request, response);
        if (manager == null) {
            return;
        }
        String action = request.getParameter("action");
        try {
            if ("sign".equals(action)) {
                repository().createPlayer(manager.getId(), request.getParameter("firstName"),
                        request.getParameter("lastName"), request.getParameter("position"));
                setFlash(request, "success", "Player signed.");
            } else if ("unsign".equals(action)) {
                repository().unsignPlayer(manager.getId(), parseRequiredInt(request, "playerId"));
                setFlash(request, "success", "Player unsigned.");
            } else {
                setFlash(request, "error", "Unsupported team action.");
            }
        } catch (RepositoryException e) {
            setFlash(request, "error", e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/team");
    }
}
