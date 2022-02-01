package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    TextView name,consis,left,goal_lft_pert;
    String id = "";
    String currentUserID;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    public static int confirmation = 0;
    DatabaseReference RootRef,HelloREf;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
    private Handler handler = new Handler();
    private Runnable runnable;
    ExtendedFloatingActionButton extendedFloatingActionButton;
    private String EVENT_DATE_TIME = "null";
    private String DATE_FORMAT = "dd/M/yyyy hh:mm:ss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        showProgressDialog();
        Intent intent = getIntent();
        id = intent.getStringExtra("LISTKEY");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");
        HelloREf = FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active").child(id).child("Win");

        name = findViewById(R.id.desc_goal_name);
        extendedFloatingActionButton = findViewById(R.id.share_Sss);
        consis = findViewById(R.id.desc_goal_const);
        left = findViewById(R.id.desc_goal_left);
        goal_lft_pert = findViewById(R.id.desc_goal_leftper);

        recyclerView = findViewById(R.id.history_recyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));



        RetriveData();

        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                share(screenShot(rootView));
            }
        });





    }

    // Here is the second progress Dialog Box
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_diaglog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//        Runnable progressRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (confirmation != 1) {
//                    progressDialog.cancel();
//                    Toast.makeText(DashboardActivity.this, "Fetching data from Firebase", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//
//        Handler pdCanceller = new Handler();
//        pdCanceller.postDelayed(progressRunnable, 5000);
    }


    private Bitmap screenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void share(Bitmap bitmap){
        String pathofBmp=
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        bitmap,"title", null);
        Uri uri = Uri.parse(pathofBmp);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Star App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "hello hello"));
    }




    @Override
    public void onStart() {
        super.onStart ();

        FirebaseRecyclerOptions<HistoryClass> options =
                new FirebaseRecyclerOptions.Builder<HistoryClass> ()
                        .setQuery ( HelloREf,HistoryClass.class )
                        .build ();


        FirebaseRecyclerAdapter<HistoryClass, StudentViewHolder2> adapter =
                new FirebaseRecyclerAdapter<HistoryClass,StudentViewHolder2> (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final StudentViewHolder2 holder, final int position, @NonNull final HistoryClass model) {

                        String listPostKey = getRef(position).getKey();

                        holder.text.setText(listPostKey+" is your day ..");




                    }

                    @NonNull
                    @Override
                    public StudentViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view  = LayoutInflater.from ( viewGroup.getContext () ).inflate ( R.layout.history_layout,viewGroup,false );
                        StudentViewHolder2 viewHolder  = new StudentViewHolder2(  view);
                        return viewHolder;

                    }

                    @Override
                    public void onDataChanged() {
                        super.onDataChanged();



                    }


                };
        recyclerView.setAdapter ( adapter );
        adapter.startListening ();




    }












    private void RetriveData() {

       RootRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String goal_string = snapshot.child ( "GoalName" ).getValue ().toString ();
                String goal_end = snapshot.child ( "EndTime" ).getValue ().toString ();
                String goal_create = snapshot.child ( "TodayTime" ).getValue ().toString ();

                Date today = new Date();
                String todaay = simpleDateFormat.format(today);



                int count_nodes = (int) snapshot.child("Win").getChildrenCount();

                int io = 0;

                if((DayReturn(todaay,goal_create))>=0){
                    String dt = ConsistentFn(count_nodes,todaay,goal_create);
                    io = GoalCOmpleteFn(todaay,goal_create,goal_end);
                    HashMap<String,Object> onlineStat = new HashMap<> (  );
                    onlineStat.put ( "Consistency", dt);
                    RootRef.child(id)
                            .updateChildren ( onlineStat );
                }


                String goal_const = snapshot.child ( "Consistency" ).getValue ().toString ();
                int const_int = Integer.parseInt(String.valueOf(goal_const));

                PieChart mPieChart = (PieChart) findViewById(R.id.piechart1);

                mPieChart.addPieSlice(new PieModel("Done", const_int, Color.parseColor("#0F9D58")));
                mPieChart.addPieSlice(new PieModel("Not Done", (100-const_int), Color.parseColor("#DB4437")));

                mPieChart.startAnimation();

                goal_lft_pert.setText("Completed :" +String.valueOf(io)+" %");


                PieChart mPieChart2 = (PieChart) findViewById(R.id.piechart2);

                mPieChart2.addPieSlice(new PieModel("Done", io, Color.parseColor("#4285F4")));
                mPieChart2.addPieSlice(new PieModel("Not Done", (100-io), Color.parseColor("#F4B400")));

                mPieChart2.startAnimation();


                name.setText(goal_string);
                consis.setText("Consistency :" +goal_const+" %");
                EVENT_DATE_TIME = goal_end;
                countDownStart();

                progressDialog.dismiss();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int GoalCOmpleteFn(String todaay, String goal_create, String goal_end) {

        float gh = (DayReturn(todaay,goal_create)+1)*100/(DayReturn(goal_end,goal_create)+1);
        return Math.round(gh);
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

        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/M/yyyy");
        Date date1=null,date2 = null;
        try {
            date2 = simpleDateFormat2.parse(low);
            date1 = simpleDateFormat2.parse(high);

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


    public String ConsistentFn(int node,String today_date,String create_date){


        float fl  = (float)(node*100)/(DayReturn(today_date,create_date)+1);
        int iu = Math.round(fl);
        return String.valueOf(iu);

    }

    public static class StudentViewHolder2 extends  RecyclerView.ViewHolder
    {

        TextView text;
        public StudentViewHolder2(@NonNull View itemView) {
            super ( itemView );
            text = itemView.findViewById ( R.id.history_yourday);

        }
    }


}