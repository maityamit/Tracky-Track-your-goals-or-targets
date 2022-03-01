package achivementtrackerbyamit.example.achivetracker.rank;

public class Topper {
    String Goal_Name;
    String Consistency;

    public Topper(String goal_Name, String consistency) {
        Goal_Name = goal_Name;
        Consistency = consistency;
    }

    public String getGoal_Name() {
        return Goal_Name;
    }

    public void setGoal_Name(String goal_Name) {
        Goal_Name = goal_Name;
    }

    public String getConsistency() {
        return Consistency;
    }

    public void setConsistency(String consistency) {
        Consistency = consistency;
    }
}
