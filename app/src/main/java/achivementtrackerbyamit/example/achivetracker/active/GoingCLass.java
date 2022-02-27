package achivementtrackerbyamit.example.achivetracker.active;


public class GoingCLass {



    private String Consistency,EndTime,GoalName,GoalType,TodayTime;

    public GoingCLass() {

    }

    public GoingCLass(String Consistency,String EndTime,String GoalName,String GoalType,String TodayTime) {

        this.Consistency = Consistency;
        this.EndTime = EndTime;
        this.GoalName = GoalName;
        this.GoalType = GoalType;
        this.TodayTime = TodayTime;

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

    public String getGoalType() {
        return GoalType;
    }

    public void setGoalType(String goalType) {
        GoalType = goalType;
    }

    public String getTodayTime() {
        return TodayTime;
    }

    public void setTodayTime(String todayTime) {
        TodayTime = todayTime;
    }
}