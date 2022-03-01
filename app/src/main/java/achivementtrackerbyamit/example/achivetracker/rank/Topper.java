package achivementtrackerbyamit.example.achivetracker.rank;

public class Topper {
    String Goal_Name;
    String Consistency;
    String user_image;
    String name;

    public Topper(String goal_Name, String consistency, String user_image, String name) {
        Goal_Name = goal_Name;
        Consistency = consistency;
        this.user_image = user_image;
        this.name = name;
    }

    public void setGoal_Name(String goal_Name) {
        Goal_Name = goal_Name;
    }

    public void setConsistency(String consistency) {
        Consistency = consistency;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoal_Name() {
        return Goal_Name;
    }

    public String getConsistency() {
        return Consistency;
    }
}
