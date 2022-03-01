package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.Objects;

public class GraphConsistency extends AppCompatActivity {

    DatabaseReference Rootref;
    String userID;
    AppCompatButton Back;
    TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_consistency);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Rootref = FirebaseDatabase.getInstance ().getReference ().child("Users").child(userID);
        Back = findViewById(R.id.button3);
        tx = findViewById(R.id.textView7);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GraphConsistency.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fetch = snapshot.child("Average").child("String").getValue().toString();
                String[] sp = fetch.split(";");

                tx.setText("Today's Consistency: "+sp[6]+"%");

                ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

                ValueLineSeries series = new ValueLineSeries();
                series.setColor(0xFF56B7F1);

                series.addPoint(new ValueLinePoint("null", Integer.parseInt(sp[0])));
                series.addPoint(new ValueLinePoint("7th", Integer.parseInt(sp[0])));
                series.addPoint(new ValueLinePoint("6th", Integer.parseInt(sp[1])));
                series.addPoint(new ValueLinePoint("5th", Integer.parseInt(sp[2])));
                series.addPoint(new ValueLinePoint("4th", Integer.parseInt(sp[3])));
                series.addPoint(new ValueLinePoint("3rd", Integer.parseInt(sp[4])));
                series.addPoint(new ValueLinePoint("2nd", Integer.parseInt(sp[5])));
                series.addPoint(new ValueLinePoint("1st", Integer.parseInt(sp[6])));
                series.addPoint(new ValueLinePoint("null", Integer.parseInt(sp[6])));

                mCubicValueLineChart.addSeries(series);
                mCubicValueLineChart.startAnimation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}