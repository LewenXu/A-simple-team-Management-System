package web.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import web.domain.PlayerProfile;

public final class JsonResponses {
    private JsonResponses() {
    }

    public static void writePlayers(Writer writer, List<PlayerProfile> players) throws IOException {
        writer.write("[");
        for (int i = 0; i < players.size(); i++) {
            PlayerProfile player = players.get(i);
            if (i > 0) {
                writer.write(",");
            }
            writer.write("{");
            writeProperty(writer, "id", String.valueOf(player.getId()), false);
            writer.write(",");
            writeProperty(writer, "name", player.getFullName(), true);
            writer.write(",");
            writeProperty(writer, "position", player.getPosition(), true);
            writer.write("}");
        }
        writer.write("]");
    }

    private static void writeProperty(Writer writer, String key, String value, boolean quoteValue) throws IOException {
        writer.write("\"");
        writer.write(escape(key));
        writer.write("\":");
        if (quoteValue) {
            writer.write("\"");
            writer.write(escape(value));
            writer.write("\"");
        } else {
            writer.write(value);
        }
    }

    private static String escape(String value) {
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
            case '\\':
                out.append("\\\\");
                break;
            case '"':
                out.append("\\\"");
                break;
            case '\n':
                out.append("\\n");
                break;
            case '\r':
                out.append("\\r");
                break;
            case '\t':
                out.append("\\t");
                break;
            default:
                out.append(ch);
                break;
            }
        }
        return out.toString();
    }
}
