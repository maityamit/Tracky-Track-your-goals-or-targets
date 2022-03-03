package achivementtrackerbyamit.example.achivetracker;

public class Notes {

    String Date,Note;

    public Notes() {

    }
    public Notes(String Date , String Note){
        this.Date = Date;
        this.Note = Note;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }
}
