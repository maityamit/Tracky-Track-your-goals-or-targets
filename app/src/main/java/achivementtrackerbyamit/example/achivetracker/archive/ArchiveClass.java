package achivementtrackerbyamit.example.achivetracker.archive;


public class ArchiveClass {



    private String Consistency,EndTime,GoalName;

    public ArchiveClass() {

    }

    public ArchiveClass(String Consistency, String EndTime, String GoalName) {

        this.Consistency = Consistency;
        this.EndTime = EndTime;
        this.GoalName = GoalName;

    }

    public String getConsistency() {
        return Consistency;
    }

    public void setConsistency(String consistency) {
        Consistency = consistency;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getGoalName() {
        return GoalName;
    }

    public void setGoalName(String goalName) {
        GoalName = goalName;
    }

}