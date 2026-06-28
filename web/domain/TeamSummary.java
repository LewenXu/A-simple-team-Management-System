package web.domain;

public class TeamSummary {
    private final int id;
    private final String localName;
    private final String teamName;
    private final Integer managerId;

    public TeamSummary(int id, String localName, String teamName, Integer managerId) {
        this.id = id;
        this.localName = localName;
        this.teamName = teamName;
        this.managerId = managerId;
    }

    public int getId() {
        return id;
    }

    public String getLocalName() {
        return localName;
    }

    public String getTeamName() {
        return teamName;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public boolean isManaged() {
        return managerId != null;
    }

    public String getDisplayName() {
        return localName + " " + teamName;
    }
}
