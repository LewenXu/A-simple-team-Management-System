package web.domain;

public class ManagerAccount {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final Integer teamId;

    public ManagerAccount(int id, String firstName, String lastName, Integer teamId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamId = teamId;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public boolean hasTeam() {
        return teamId != null;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
