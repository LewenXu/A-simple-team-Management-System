package web.domain;

public class PlayerProfile {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String position;
    private final Integer teamId;

    public PlayerProfile(int id, String firstName, String lastName, String position, Integer teamId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
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

    public String getPosition() {
        return position;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
