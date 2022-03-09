package achivementtrackerbyamit.example.achivetracker.archive_goal;


public class ArchiveClass {



    private String Consistency;
    private String EndTime;
    private String GoalName;
    private String deleteDate;
    private boolean autoDelete=false;

    public ArchiveClass()
    {

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

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public String getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(String deleteDate) {
        this.deleteDate = deleteDate;
    }
}