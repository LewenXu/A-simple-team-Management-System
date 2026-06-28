<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="web.domain.ManagerAccount" %>
<%@ page import="web.domain.PlayerProfile" %>
<%@ page import="web.domain.TeamSummary" %>
<%@ page import="web.util.Html" %>
<%
    String context = request.getContextPath();
    ManagerAccount manager = (ManagerAccount) request.getAttribute("manager");
    TeamSummary team = (TeamSummary) request.getAttribute("team");
    List<PlayerProfile> players = (List<PlayerProfile>) request.getAttribute("players");
    String[] positions = (String[]) request.getAttribute("positions");
    String flashType = (String) request.getAttribute("flashType");
    String flashMessage = (String) request.getAttribute("flashMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Players · <%= Html.escape(team.getDisplayName()) %></title>
    <link rel="stylesheet" href="<%= context %>/assets/app.css">
</head>
<body>
    <header class="topbar">
        <div>
            <h1><%= Html.escape(team.getDisplayName()) %></h1>
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
            <div class="section-title">
                <h2>Players</h2>
                <input id="playerSearch"
                       type="search"
                       placeholder="Search players"
                       data-endpoint="<%= context %>/api/players"
                       data-action="<%= context %>/team">
            </div>
            <% if (flashMessage != null) { %>
                <div class="alert <%= Html.escape(flashType) %>"><%= Html.escape(flashMessage) %></div>
            <% } %>
            <table>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Position</th>
                        <th class="right">Action</th>
                    </tr>
                </thead>
                <tbody id="playersBody">
                    <% for (PlayerProfile player : players) { %>
                        <tr>
                            <td><%= Html.escape(player.getFullName()) %></td>
                            <td><%= Html.escape(player.getPosition()) %></td>
                            <td class="right">
                                <form method="post" action="<%= context %>/team">
                                    <input type="hidden" name="action" value="unsign">
                                    <input type="hidden" name="playerId" value="<%= player.getId() %>">
                                    <button type="submit" class="small danger">Unsign</button>
                                </form>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        </section>

        <aside class="band">
            <h2>Sign New Player</h2>
            <form method="post" action="<%= context %>/team" class="stack">
                <input type="hidden" name="action" value="sign">
                <label for="firstName">First name</label>
                <input id="firstName" name="firstName" type="text" required>
                <label for="lastName">Last name</label>
                <input id="lastName" name="lastName" type="text" required>
                <label for="position">Position</label>
                <select id="position" name="position" required>
                    <% for (String position : positions) { %>
                        <option value="<%= Html.escape(position) %>"><%= Html.escape(position) %></option>
                    <% } %>
                </select>
                <button type="submit">Sign Player</button>
            </form>
        </aside>
    </main>
    <script src="<%= context %>/assets/team.js"></script>
</body>
</html>
