package web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.domain.ManagerAccount;
import web.repository.RepositoryException;

public class SwapTeamServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ManagerAccount manager = requireManager(request, response);
        if (manager == null) {
            return;
        }
        exposeFlash(request);
        request.setAttribute("manager", manager);
        request.setAttribute("team", repository().findTeamForManager(manager.getId()));
        request.setAttribute("availableTeams", repository().findAvailableTeamsForManager(manager.getId()));
        forward(request, response, "swap.jsp");
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
            if ("withdraw".equals(action)) {
                repository().withdrawManager(manager.getId());
                setFlash(request, "success", "You withdrew from your team.");
            } else if ("assign".equals(action)) {
                repository().assignManagerToTeam(manager.getId(), parseRequiredInt(request, "teamId"));
                setFlash(request, "success", "Team assignment updated.");
            } else {
                setFlash(request, "error", "Unsupported team assignment action.");
            }
        } catch (RepositoryException e) {
            setFlash(request, "error", e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
