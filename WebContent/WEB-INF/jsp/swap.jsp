<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="web.domain.ManagerAccount" %>
<%@ page import="web.domain.TeamSummary" %>
<%@ page import="web.util.Html" %>
<%
    String context = request.getContextPath();
    ManagerAccount manager = (ManagerAccount) request.getAttribute("manager");
    TeamSummary team = (TeamSummary) request.getAttribute("team");
    List<TeamSummary> availableTeams = (List<TeamSummary>) request.getAttribute("availableTeams");
    String flashType = (String) request.getAttribute("flashType");
    String flashMessage = (String) request.getAttribute("flashMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Team Assignment</title>
    <link rel="stylesheet" href="<%= context %>/assets/app.css">
</head>
<body>
    <header class="topbar">
        <div>
            <h1>Team Assignment</h1>
            <p><%= Html.escape(manager.getFullName()) %></p>
        </div>
        <nav>
            <a href="<%= context %>/dashboard">Dashboard</a>
            <a href="<%= context %>/team">Players</a>
            <a href="<%= context %>/swap">Teams</a>
            <form method="post" action="<%= context %>/logout">
                <button type="submit" class="link-button">Logout</button>
            </form>
        </nav>
    </header>

    <main class="page two-column">
        <section class="band">
            <h2>Current Team</h2>
            <% if (flashMessage != null) { %>
                <div class="alert <%= Html.escape(flashType) %>"><%= Html.escape(flashMessage) %></div>
            <% } %>
            <% if (team == null) { %>
                <p class="muted">You are not assigned to a team.</p>
            <% } else { %>
                <p class="large"><%= Html.escape(team.getDisplayName()) %></p>
                <form method="post" action="<%= context %>/swap">
                    <input type="hidden" name="action" value="withdraw">
                    <button type="submit" class="danger">Withdraw</button>
                </form>
            <% } %>
        </section>

        <section class="band">
            <h2>Available Teams</h2>
            <% if (availableTeams == null || availableTeams.isEmpty()) { %>
                <p class="muted">There are no unmanaged teams available.</p>
            <% } else { %>
                <form method="post" action="<%= context %>/swap" class="stack">
                    <input type="hidden" name="action" value="assign">
                    <label for="teamId">Team</label>
                    <select id="teamId" name="teamId" required>
                        <% for (TeamSummary available : availableTeams) { %>
                            <option value="<%= available.getId() %>"><%= Html.escape(available.getDisplayName()) %></option>
                        <% } %>
                    </select>
                    <button type="submit">Assign Selected Team</button>
                </form>
            <% } %>
        </section>
    </main>
</body>
</html>
