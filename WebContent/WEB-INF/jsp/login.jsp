<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="web.util.Html" %>
<%
    String context = request.getContextPath();
    String flashType = (String) request.getAttribute("flashType");
    String flashMessage = (String) request.getAttribute("flashMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Team Management Login</title>
    <link rel="stylesheet" href="<%= context %>/assets/app.css">
</head>
<body class="auth-page">
    <main class="auth-shell">
        <section class="auth-panel">
            <h1>Team Management</h1>
            <p class="muted">Manager access</p>
            <% if (flashMessage != null) { %>
                <div class="alert <%= Html.escape(flashType) %>"><%= Html.escape(flashMessage) %></div>
            <% } %>
            <form method="post" action="<%= context %>/login" class="stack">
                <label for="managerId">Manager ID</label>
                <input id="managerId" name="managerId" type="text" inputmode="numeric" autocomplete="username" required autofocus>
                <button type="submit">Login</button>
            </form>
            <p class="hint">Seeded IDs: 12345, 1, 34896, 678, 912</p>
        </section>
    </main>
</body>
</html>
