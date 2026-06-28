package web.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import web.domain.ManagerAccount;
import web.listener.ApplicationContextListener;
import web.repository.LeagueRepository;
import web.repository.RepositoryException;

public abstract class BaseServlet extends HttpServlet {
    protected LeagueRepository repository() {
        Object repository = getServletContext().getAttribute(ApplicationContextListener.REPOSITORY_ATTRIBUTE);
        if (!(repository instanceof LeagueRepository)) {
            throw new RepositoryException("Application repository has not been initialised.");
        }
        return (LeagueRepository) repository;
    }

    protected ManagerAccount currentManager(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object managerId = session.getAttribute("managerId");
        if (!(managerId instanceof Integer)) {
            return null;
        }
        return repository().refreshManager(((Integer) managerId).intValue());
    }

    protected ManagerAccount requireManager(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ManagerAccount manager = currentManager(request);
        if (manager == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return manager;
    }

    protected void forward(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/" + jsp);
        dispatcher.forward(request, response);
    }

    protected void setFlash(HttpServletRequest request, String type, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }

    protected void exposeFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        request.setAttribute("flashType", session.getAttribute("flashType"));
        request.setAttribute("flashMessage", session.getAttribute("flashMessage"));
        session.removeAttribute("flashType");
        session.removeAttribute("flashMessage");
    }

    protected int parseRequiredInt(HttpServletRequest request, String parameterName) {
        String value = request.getParameter(parameterName);
        if (value == null || value.trim().isEmpty()) {
            throw new RepositoryException(parameterName + " is required.");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new RepositoryException(parameterName + " must be an integer.");
        }
    }
}
