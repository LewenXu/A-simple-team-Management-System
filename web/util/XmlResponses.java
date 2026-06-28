package web.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import web.domain.PlayerProfile;
import web.domain.TeamSummary;
import web.repository.LeagueRepository;

public final class XmlResponses {
    private XmlResponses() {
    }

    public static void writeTeams(Writer writer, LeagueRepository repository) throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<league>\n");
        for (TeamSummary team : repository.findAllTeams()) {
            writer.write("  <team id=\"");
            writer.write(String.valueOf(team.getId()));
            writer.write("\" localName=\"");
            writer.write(escape(team.getLocalName()));
            writer.write("\" name=\"");
            writer.write(escape(team.getTeamName()));
            writer.write("\"");
            if (team.getManagerId() != null) {
                writer.write(" managerId=\"");
                writer.write(String.valueOf(team.getManagerId()));
                writer.write("\"");
            }
            writer.write(">\n");
            writePlayers(writer, repository.findPlayersForTeam(team.getId()));
            writer.write("  </team>\n");
        }
        writer.write("</league>\n");
    }

    private static void writePlayers(Writer writer, List<PlayerProfile> players) throws IOException {
        writer.write("    <players>\n");
        for (PlayerProfile player : players) {
            writer.write("      <player id=\"");
            writer.write(String.valueOf(player.getId()));
            writer.write("\" position=\"");
            writer.write(escape(player.getPosition()));
            writer.write("\">");
            writer.write("<firstName>");
            writer.write(escape(player.getFirstName()));
            writer.write("</firstName>");
            writer.write("<lastName>");
            writer.write(escape(player.getLastName()));
            writer.write("</lastName>");
            writer.write("</player>\n");
        }
        writer.write("    </players>\n");
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
            case '&':
                out.append("&amp;");
                break;
            case '<':
                out.append("&lt;");
                break;
            case '>':
                out.append("&gt;");
                break;
            case '"':
                out.append("&quot;");
                break;
            case '\'':
                out.append("&apos;");
                break;
            default:
                out.append(ch);
                break;
            }
        }
        return out.toString();
    }
}
