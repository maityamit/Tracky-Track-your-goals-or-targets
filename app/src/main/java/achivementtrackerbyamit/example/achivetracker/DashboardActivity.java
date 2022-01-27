package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    TextView name,consis,left;
    String id = "";
    String currentUserID;
    DatabaseReference RootRef;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
    private Handler handler = new Handler();
    private Runnable runnable;
    private String EVENT_DATE_TIME = "null";
    private String DATE_FORMAT = "dd/M/yyyy hh:mm:ss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();
        id = intent.getStringExtra("LISTKEY");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");

        name = findViewById(R.id.desc_goal_name);
        consis = findViewById(R.id.desc_goal_const);
        left = findViewById(R.id.desc_goal_left);




        RetriveData();





    }

    private void RetriveData() {

       RootRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String goal_string = snapshot.child ( "GoalName" ).getValue ().toString ();
                String goal_const = snapshot.child ( "Consistency" ).getValue ().toString ();
                String goal_end = snapshot.child ( "EndTime" ).getValue ().toString ();
                String goal_create = snapshot.child ( "TodayTime" ).getValue ().toString ();

                name.setText(goal_string);
                consis.setText(goal_const);
                EVENT_DATE_TIME = goal_end;
                countDownStart();


                int const_int = Integer.parseInt(String.valueOf(goal_const));

                PieChart mPieChart = (PieChart) findViewById(R.id.piechart1);

                mPieChart.addPieSlice(new PieModel("Done", const_int, Color.parseColor("#558B2F")));
                mPieChart.addPieSlice(new PieModel("Not Done", (100-const_int), Color.parseColor("#C62828")));

                mPieChart.startAnimation();






                Date today = new Date();
                String todaay = simpleDateFormat.format(today);

                float hu = (float)((DayReturn(todaay,goal_create)*100)/DayReturn(goal_end,goal_create));
                int io = Math.round(hu);


                PieChart mPieChart2 = (PieChart) findViewById(R.id.piechart2);

                mPieChart2.addPieSlice(new PieModel("Done", io, Color.parseColor("#558B2F")));
                mPieChart2.addPieSlice(new PieModel("Not Done", (100-io), Color.parseColor("#C62828")));

                mPieChart2.startAnimation();







            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void countDownStart() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date event_date = dateFormat.parse(EVENT_DATE_TIME);
                    Date current_date = new Date();
                    if (!current_date.after(event_date)) {
                        long diff = event_date.getTime() - current_date.getTime();
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        //
                        left.setText(String.format("%02d",Days)+" days "+String.format("%02d", Hours)+" hours "+String.format("%02d", Minutes)+" minutes "+String.format("%02d", Seconds)+" seconds ");

                    } else {

                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    public  long DayReturn(String high,String low){
        Date date1=null,date2 = null;
        try {
            date2 = simpleDateFormat.parse(low);
            date1 = simpleDateFormat.parse(high);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        long different = date1.getTime() - date2.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        return elapsedDays;
    }


}