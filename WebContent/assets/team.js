(function () {
    var search = document.getElementById("playerSearch");
    var body = document.getElementById("playersBody");
    if (!search || !body) {
        return;
    }

    var endpoint = search.getAttribute("data-endpoint");
    var action = search.getAttribute("data-action");
    var timer = null;

    function escapeHtml(value) {
        return String(value).replace(/[&<>"']/g, function (ch) {
            return {
                "&": "&amp;",
                "<": "&lt;",
                ">": "&gt;",
                "\"": "&quot;",
                "'": "&#39;"
            }[ch];
        });
    }

    function render(players) {
        if (!players.length) {
            body.innerHTML = "<tr><td colspan=\"3\" class=\"muted\">No players found.</td></tr>";
            return;
        }
        body.innerHTML = players.map(function (player) {
            return "<tr>"
                + "<td>" + escapeHtml(player.name) + "</td>"
                + "<td>" + escapeHtml(player.position) + "</td>"
                + "<td class=\"right\">"
                + "<form method=\"post\" action=\"" + escapeHtml(action) + "\">"
                + "<input type=\"hidden\" name=\"action\" value=\"unsign\">"
                + "<input type=\"hidden\" name=\"playerId\" value=\"" + escapeHtml(player.id) + "\">"
                + "<button type=\"submit\" class=\"small danger\">Unsign</button>"
                + "</form>"
                + "</td>"
                + "</tr>";
        }).join("");
    }

    function loadPlayers() {
        var query = encodeURIComponent(search.value);
        fetch(endpoint + "?q=" + query, {headers: {"Accept": "application/json"}})
            .then(function (response) {
                if (!response.ok) {
                    throw new Error("Could not load players");
                }
                return response.json();
            })
            .then(render)
            .catch(function () {
                body.innerHTML = "<tr><td colspan=\"3\" class=\"muted\">Player search is unavailable.</td></tr>";
            });
    }

    search.addEventListener("input", function () {
        window.clearTimeout(timer);
        timer = window.setTimeout(loadPlayers, 180);
    });
}());
