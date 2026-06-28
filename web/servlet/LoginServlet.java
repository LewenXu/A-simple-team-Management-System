package web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.domain.ManagerAccount;

public class LoginServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (currentManager(request) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        exposeFlash(request);
        forward(request, response, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String value = request.getParameter("managerId");
        try {
            int managerId = Integer.parseInt(value == null ? "" : value.trim());
            ManagerAccount manager = repository().findManagerById(managerId);
            if (manager == null) {
                request.setAttribute("flashType", "error");
                request.setAttribute("flashMessage", "Invalid login credentials.");
                forward(request, response, "login.jsp");
                return;
            }
            request.getSession(true).setAttribute("managerId", Integer.valueOf(manager.getId()));
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (NumberFormatException e) {
            request.setAttribute("flashType", "error");
            request.setAttribute("flashMessage", "Manager ID must be an integer.");
            forward(request, response, "login.jsp");
        }
    }
}
