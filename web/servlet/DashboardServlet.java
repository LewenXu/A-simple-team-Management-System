package web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.domain.ManagerAccount;

public class DashboardServlet extends BaseServlet {
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
        request.setAttribute("teams", repository().findAllTeams());
        request.setAttribute("availableTeams", repository().findAvailableTeamsForManager(manager.getId()));
        request.setAttribute("persistenceWarning", getServletContext().getAttribute("persistenceWarning"));
        request.setAttribute("persistenceMode", getServletContext().getAttribute("persistenceMode"));
        forward(request, response, "dashboard.jsp");
    }
}
