package achivementtrackerbyamit.example.achivetracker.active_goal;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import achivementtrackerbyamit.example.achivetracker.DashboardActivity;
import achivementtrackerbyamit.example.achivetracker.HomeActivity;
import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.alarm.AlarmReceiver;
import achivementtrackerbyamit.example.achivetracker.archive_goal.ArchiveClass;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

// Custom adapter for the RecyclerView for displaying goals
public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.StudentViewHolder2> {

    // List of goals, goals are stored as a pair of the key of the goal and the goal object
    ArrayList<Pair<String,GoingCLass>> goalList;
    ActiveGoalFragment fragment;

    GoalAdapter(ActiveGoalFragment fragment, ArrayList<Pair<String,GoingCLass>> goalList)
    {
        this.fragment=fragment;
        this.goalList=goalList;
    }

    @NonNull
    @Override
    public StudentViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.main_tiles_layout,parent,false);
        return new StudentViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder2 holder, int position) {




        // Key of the goal is the first element of the pair
        String listPostKey = goalList.get(position).first;
        DatabaseReference db = fragment.RootRef.child(listPostKey);
        StringBuilder sb = new StringBuilder();
        StringBuilder retriveBreakEndDate = new StringBuilder();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild("Status") && snapshot.hasChild("BreakEndDate")) {

                    sb.append(snapshot.child("Status").getValue().toString());

                    if((sb.toString()).equals("OnBreak")) {
                        holder.check_in_layout.setVisibility(View.GONE);
                    }

                    retriveBreakEndDate.append(snapshot.child("BreakEndDate").getValue().toString());

                    SimpleDateFormat newFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");


                    Date today1 = new Date();
                    String todayDate = newFormat.format(today1);



                    Date date_1 = null, date_2 = null;
                    try {
                        date_1 = newFormat.parse(todayDate);
                        date_2 = newFormat.parse(retriveBreakEndDate.toString());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long d_ifferent = date_1.getTime() - date_2.getTime();
                    long s_econdsInMilli = 1000;
                    long m_inutesInMilli = s_econdsInMilli * 60;
                    long h_oursInMilli = m_inutesInMilli * 60;
                    long d_aysInMilli = h_oursInMilli * 24;

                    long e_lapsedDays = d_ifferent / d_aysInMilli;

                    if(e_lapsedDays==1) {
                        DatabaseReference tempDB = fragment.RootRef.child(listPostKey);
                        tempDB.child("Status").setValue("Active");
                        tempDB.child("BreakEndDate").removeValue();
                    }
                }

                SimpleDateFormat newFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                SimpleDateFormat newFormat1 = new SimpleDateFormat("dd/M/yyyy");
                String End = snapshot.child("EndTime").getValue().toString();
                String name = "1 day left to complete your Goal: " + snapshot.child("GoalName").getValue().toString();
                Date t = new Date();
                String t1 = newFormat.format(t);
                try {
                    Date today = newFormat.parse(t1);
                    Date end = newFormat.parse(End);
                    Date Send = newFormat1.parse(End);

                    long d_ifferent = end.getTime() - today.getTime();
                    long s_econdsInMilli = 1000;
                    long m_inutesInMilli = s_econdsInMilli * 60;
                    long h_oursInMilli = m_inutesInMilli * 60;
                    long d_aysInMilli = h_oursInMilli * 24;

                    long e_lapsedDays = d_ifferent / d_aysInMilli;

                    if(e_lapsedDays>0) {
                        String s = End.substring(0,9) + " 23:59:59";
                        createAlarm(s.trim(), name);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //   Toast.makeText(fragment.getContext(), ""+retriveBreakEndDate, Toast.LENGTH_SHORT).show();
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-M-yyyy");

        // Goal object is the second element of the pair
        GoingCLass model=goalList.get(position).second;


        // Same code as the old adapter
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

        Date today = new Date();
        String todaay = simpleDateFormat.format(today);
        String jys_da = simpleDateFormat2.format(today);

        holder.goal_name.setText(model.getGoalName());

        if (model.getGoalType().equals("High")){
            holder.priority.setImageDrawable(fragment.getContext().getResources().getDrawable(R.drawable.rd));
        }else if (model.getGoalType().equals("Medium")) {
            holder.priority.setImageDrawable(fragment.getContext().getResources().getDrawable(R.drawable.yl));
        }else{
            holder.priority.setImageDrawable(fragment.getContext().getResources().getDrawable(R.drawable.gr));
        }


      //  holder.const_text.setText("Consistency :" +model.getConsistency()+" %");


        // Retrieve Goal Image into CurrentGoal Frag Recyclerview items
        fragment.RootRef.child(listPostKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("goal_image")){
                    String imageuri = snapshot.child("goal_image").getValue().toString();
                    Picasso.get().load(imageuri).placeholder(R.drawable.goals).error(R.drawable.goals).into(holder.goalimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        fragment.RootRef.child(listPostKey).child("Win").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(jys_da)) {
                    holder.check_in_layout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Date date1=null,date2 = null;
        try {
            date1 = simpleDateFormat.parse(todaay);
            date2 = simpleDateFormat.parse(model.getEndTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        long different = date2.getTime() - date1.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        // TODO: implemented (Complete)

        if(different<0){

            fragment.postDataIntoArchive();
            ArchiveClass goal= new ArchiveClass(model.getConsistency(), model.getEndTime(), model.getGoalName());

            fragment.archiveDataRef.child(String.valueOf(fragment.maxId+1)).setValue(goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Delete the data from current fragment
                    fragment.deleteArchieveData(listPostKey);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }

        if (elapsedDays==1){
            holder.left_day.setText(elapsedDays+" day"+"\nleft");
        }
        else{
            holder.left_day.setText(elapsedDays + " days" + "\nleft");
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getContext(),DashboardActivity.class);
                intent.putExtra("LISTKEY",listPostKey);
                fragment.startActivity(intent);
            }
        });

        holder.checkBox_true.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Material Alert Dialog Box Added
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getContext(),R.style.AlertDialogTheme1);
                    builder.setTitle("Alert!");
                    builder.setMessage("Confirm Goal Completion?");
                    builder.setBackground(fragment.getResources().getDrawable(R.drawable.material_dialog_box , null));
                    builder.setCancelable(false);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //For True
                            // perform logic
                            HashMap<String, Object> onlineStat = new HashMap<>();
                            onlineStat.put("Value", "true");
                            fragment.RootRef.child(listPostKey).child("Win").child(jys_da)
                                    .updateChildren(onlineStat);
                            holder.checkBox_true.setVisibility(View.INVISIBLE);

                            fragment.RootRef.child(listPostKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int count_nodes = (int) snapshot.child("Win").getChildrenCount();
                                    if((DayReturn(todaay,model.getTodayTime()))>=0){
                                        String dt = ConsistentFn(count_nodes,todaay,model.getTodayTime());
                                        HashMap<String,Object> onlineStat = new HashMap<> (  );
                                        onlineStat.put ( "Consistency", dt);
                                        fragment.RootRef.child(listPostKey)
                                                .updateChildren ( onlineStat );

                                        showDialog();


                                        long sum = fragment.sum, count = fragment.count;
                                        sum+=Long.parseLong(dt);
                                        long avg = sum/count;
                                        Toast.makeText(fragment.getContext(), ""+avg, Toast.LENGTH_SHORT).show();
                                        fragment.RootRefUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String fetchD, fetchS, td;
                                                fetchD = snapshot.child("Average").child("PDate").getValue().toString();
                                                fetchS = snapshot.child("Average").child("String").getValue().toString();
                                                SimpleDateFormat newFormat = new SimpleDateFormat("dd/M/yyyy");
                                                Date t = new Date();
                                                td = newFormat.format(t);
                                                try {
                                                    Date PDate = newFormat.parse(fetchD);
                                                    Date today = newFormat.parse(td);

                                                    long xf = PDate.getTime() - today.getTime();
                                                    long s_econdsInMilli = 1000;
                                                    long m_inutesInMilli = s_econdsInMilli * 60;
                                                    long h_oursInMilli = m_inutesInMilli * 60;
                                                    long d_aysInMilli = h_oursInMilli * 24;

                                                    long diff = xf / d_aysInMilli;
                                                    if(diff==0) {
                                                        String[] sp = fetchS.split(";");
                                                        sp[sp.length-1] = String.valueOf(avg);

                                                        String up = "";
                                                        for(int i=0; i<6; i++) {
                                                            up+=sp[i]+";";
                                                        }
                                                        up+=sp[sp.length-1];

                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("Average/String", up);
                                                        fragment.RootRefUpdate.updateChildren ( map );
                                                    }

                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            String key= fragment.activityRef.push().getKey();
                            String value= "Checked in "+model.getGoalName()+" on "+todaay;
                            fragment.activityRef.child(key).setValue(value, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    //Updating Average Node

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //False
                            dialog.cancel(); //Removes AlertDialog Box
                            holder.checkBox_true.setChecked(false);
                        }
                    });
                    builder.show();
                } else {
                    //Fail
                    Toast.makeText(fragment.getContext(), "Not Checked", Toast.LENGTH_SHORT).show(); //Just to Inform the user
                }
            }
        });

        // Set completed goal percentage on the progress bar
        holder.completedBar.setProgress(DashboardActivity.GoalCOmpleteFn(todaay,model.getTodayTime(),model.getEndTime()),true);
    }

    private void showDialog() {

        Dialog dialog;
        //Create the Dialog here
        dialog = new Dialog(fragment.getContext());
        dialog.setContentView(R.layout.dialog_fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(fragment.getContext().getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = dialog.findViewById(R.id.btn_okay);
        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Thread td = new Thread(){
                    public void run(){
                        try{
                            sleep(2000);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                        finally {
                        }
                    }
                };td.start();

            }
        });
        dialog.show();
    }

    public String ConsistentFn(int node,String today_date,String create_date){


        float fl  = (float)(node*100)/(DayReturn(today_date,create_date)+1);
        int iu = Math.round(fl);
        return String.valueOf(iu);

    }

    public static long DayReturn(String high,String low){

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

        return elapsedDays;
    }


    private void createAlarm(String end, String name) {
        //   Toast.makeText(fragment.getContext(), end, Toast.LENGTH_SHORT).show();
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date send = null;
        try {
            send = newFormat.parse(end);
            cal.setTime(send);
            cal.add(Calendar.DAY_OF_MONTH, -1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AlarmManager alarmManager = (AlarmManager) fragment.getContext().getSystemService(Context.ALARM_SERVICE); //Creating Alarm Manager
        Intent intent = new Intent(fragment.getContext(), AlarmReceiver.class);
        intent.putExtra("goal", name); //passing Goal name
        PendingIntent pendingIntent = PendingIntent.getBroadcast(fragment.getContext(), 1, intent, 0); //pending intent
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent); //wakes device if sleep
    }


    @Override
    public int getItemCount() {
        return goalList.size();
    }

    // Changing the list when searching is carried out and notifying the change
    public void setGoalList(ArrayList<Pair<String,GoingCLass>> list)
    {
        goalList= list;
        notifyDataSetChanged();
    }


    // Same ViewHolder as the old adapter
    public class StudentViewHolder2 extends  RecyclerView.ViewHolder
    {

        TextView goal_name,goal_priority,left_day,const_text;
        LinearLayout check_in_layout;
        CheckBox checkBox_true;
        ProgressBar completedBar;
        ImageView goalimage, priority;
        public StudentViewHolder2(@NonNull View itemView) {
            super ( itemView );
            goal_name = itemView.findViewById ( R.id.lay_goal_name);
       //     goal_priority = itemView.findViewById ( R.id.lay_goal_priority);
            left_day = itemView.findViewById ( R.id.lay_goal_left);
            priority = itemView.findViewById(R.id.prio);

            check_in_layout = itemView.findViewById(R.id.check_in_layout);
            //const_text = itemView.findViewById ( R.id.lay_goal_const);
            checkBox_true = itemView.findViewById ( R.id.true_checkbox);

            completedBar = itemView.findViewById(R.id.completed_progress);
            goalimage = itemView.findViewById(R.id.goalpic);
        }
    }
}