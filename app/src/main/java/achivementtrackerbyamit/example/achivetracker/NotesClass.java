package achivementtrackerbyamit.example.achivetracker;

public class NotesClass {

    String Date,Note;

    public NotesClass() {

    }
    public NotesClass(String Date , String Note){
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
