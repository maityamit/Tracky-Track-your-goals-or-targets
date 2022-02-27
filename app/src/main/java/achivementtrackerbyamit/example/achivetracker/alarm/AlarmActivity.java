package achivementtrackerbyamit.example.achivetracker.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import achivementtrackerbyamit.example.achivetracker.HomeActivity;
import achivementtrackerbyamit.example.achivetracker.R;

public class AlarmActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Button DatePick, TimePick, SetAlarm;
    TextView showT, showD;
    Calendar set = Calendar.getInstance();
    StringBuilder sb = new StringBuilder();
    private NotificationHelper mNotificationHelper;
    SimpleDateFormat dateFormat, timeFormat;
    String time, calendarDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        DatePick = findViewById(R.id.DatePick);
        showD = findViewById(R.id.DateView);
        TimePick = findViewById(R.id.TimePick);
        showT = findViewById(R.id.TimeView);
        SetAlarm = findViewById(R.id.SetAlarm);

        Intent intent = getIntent();
        String GoalName = intent.getExtras().getString("GoalName"); //Fetching Goal Name from Intent
        sb.append(GoalName);

        mNotificationHelper = new NotificationHelper(this, GoalName); //initializing NotificationHelper Class with Parameters

        DatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFrag(); //Pop up Date Picker Dialog
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        TimePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFrag(); //Pop up Time Picker Dialog
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        set.set(Calendar.YEAR, i); //Set all data in Global Variable Calendar instance
        set.set(Calendar.MONTH, i1);
        set.set(Calendar.DATE, i2);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy"); //Date Format
        calendarDate = dateFormat.format(set.getTime());
        showD.setText(calendarDate); //setting TextView
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        set.set(Calendar.HOUR_OF_DAY, i); //Set all data in Global Variable Calendar instance
        set.set(Calendar.MINUTE, i1);
        timeFormat = new SimpleDateFormat("HH-mm aa"); //Time Format
        time = timeFormat.format(set.getTime());
        showT.setText(time); //Setting TextView
    }


    public void SetAlarmMan(View view) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); //Creating Alarm Manager
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("goal", sb.toString()); //passing Goal name
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0); //pending intent
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, set.getTimeInMillis(), pendingIntent); //wakes device if sleep
    }

    public void cancelAlarm(View view) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); //new object
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0); //pending intent

        alarmManager.cancel(pendingIntent); //cancels alarm
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AlarmActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}