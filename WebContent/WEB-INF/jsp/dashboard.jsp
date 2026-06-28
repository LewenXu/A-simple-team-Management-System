<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="web.domain.ManagerAccount" %>
<%@ page import="web.domain.TeamSummary" %>
<%@ page import="web.util.Html" %>
<%
    String context = request.getContextPath();
    ManagerAccount manager = (ManagerAccount) request.getAttribute("manager");
    TeamSummary team = (TeamSummary) request.getAttribute("team");
    List<TeamSummary> teams = (List<TeamSummary>) request.getAttribute("teams");
    List<TeamSummary> availableTeams = (List<TeamSummary>) request.getAttribute("availableTeams");
    String flashType = (String) request.getAttribute("flashType");
    String flashMessage = (String) request.getAttribute("flashMessage");
    Object persistenceWarning = request.getAttribute("persistenceWarning");
    Object persistenceMode = request.getAttribute("persistenceMode");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manager Dashboard</title>
    <link rel="stylesheet" href="<%= context %>/assets/app.css">
</head>
<body>
    <header class="topbar">
        <div>
            <h1>Manager Dashboard</h1>
            <p><%= Html.escape(manager.getFullName()) %> · ID <%= manager.getId() %></p>
        </div>
        <nav>
            <a href="<%= context %>/dashboard">Dashboard</a>
            <a href="<%= context %>/team">Players</a>
            <a href="<%= context %>/swap">Teams</a>
            <a href="<%= context %>/api/teams.xml" target="_blank">XML</a>
            <form method="post" action="<%= context %>/logout">
                <button type="submit" class="link-button">Logout</button>
            </form>
        </nav>
    </header>

    <main class="page">
        <% if (flashMessage != null) { %>
            <div class="alert <%= Html.escape(flashType) %>"><%= Html.escape(flashMessage) %></div>
        <% } %>
        <% if (persistenceWarning != null) { %>
            <div class="alert warning"><%= Html.escape(persistenceWarning) %></div>
        <% } %>

        <section class="band summary-grid">
            <div>
                <span class="eyebrow">Current Team</span>
                <% if (team == null) { %>
                    <h2>No team assigned</h2>
                    <p class="muted">Choose an available team to begin managing players.</p>
                <% } else { %>
                    <h2><%= Html.escape(team.getDisplayName()) %></h2>
                    <p class="muted">You can sign and unsign players from the player management page.</p>
                <% } %>
            </div>
            <div class="actions">
                <% if (team != null) { %>
                    <a class="button" href="<%= context %>/team">Manage Players</a>
                <% } %>
                <a class="button secondary" href="<%= context %>/swap">Swap Team</a>
            </div>
        </section>

        <section class="band">
            <div class="section-title">
                <h2>Available Teams</h2>
                <span class="badge"><%= availableTeams == null ? 0 : availableTeams.size() %> open</span>
            </div>
            <% if (availableTeams == null || availableTeams.isEmpty()) { %>
                <p class="muted">There are no unmanaged teams available.</p>
            <% } else { %>
                <form method="post" action="<%= context %>/swap" class="inline-form">
                    <input type="hidden" name="action" value="assign">
                    <select name="teamId" required>
                        <% for (TeamSummary available : availableTeams) { %>
                            <option value="<%= available.getId() %>"><%= Html.escape(available.getDisplayName()) %></option>
                        <% } %>
                    </select>
                    <button type="submit">Assign Team</button>
                </form>
            <% } %>
        </section>

        <section class="band">
            <div class="section-title">
                <h2>League Teams</h2>
                <span class="badge">Persistence: <%= Html.escape(persistenceMode) %></span>
            </div>
            <table>
                <thead>
                    <tr>
                        <th>Local Name</th>
                        <th>Team</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (TeamSummary row : teams) { %>
                        <tr>
                            <td><%= Html.escape(row.getLocalName()) %></td>
                            <td><%= Html.escape(row.getTeamName()) %></td>
                            <td><%= row.isManaged() ? "Managed" : "Available" %></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </section>
    </main>
</body>
</html>
