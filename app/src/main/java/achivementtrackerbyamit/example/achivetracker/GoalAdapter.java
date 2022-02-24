package achivementtrackerbyamit.example.achivetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import achivementtrackerbyamit.example.achivetracker.archive.ArchiveClass;

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
            holder.goal_priority.setText("Priority: "+model.getGoalType());
            holder.goal_type_layout.setBackgroundColor(Color.parseColor("#FFD7D7"));
            holder.goal_priority.setTextColor(Color.parseColor("#FF0000"));
        }else if (model.getGoalType().equals("Medium")) {
            holder.goal_priority.setText("Priority: "+model.getGoalType());
            holder.goal_type_layout.setBackgroundColor(Color.parseColor("#FDFFD7"));
            holder.goal_priority.setTextColor(Color.parseColor("#FFE000"));
        }else{
            holder.goal_priority.setText("Priority: "+model.getGoalType());
            holder.goal_type_layout.setBackgroundColor(Color.parseColor("#98FF9D"));
            holder.goal_priority.setTextColor(Color.parseColor("#00C325"));
        }


        holder.const_text.setText("Consistency :" +model.getConsistency()+" %");


        
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
        RelativeLayout goal_type_layout;
        LinearLayout check_in_layout;
        CheckBox checkBox_true;
        ProgressBar completedBar;
        ImageView goalimage;
        public StudentViewHolder2(@NonNull View itemView) {
            super ( itemView );
            goal_name = itemView.findViewById ( R.id.lay_goal_name);
            goal_priority = itemView.findViewById ( R.id.lay_goal_priority);
            left_day = itemView.findViewById ( R.id.lay_goal_left);
            goal_type_layout = itemView.findViewById(R.id.goal_type_layout);

            check_in_layout = itemView.findViewById(R.id.check_in_layout);
            const_text = itemView.findViewById ( R.id.lay_goal_const);
            checkBox_true = itemView.findViewById ( R.id.true_checkbox);

            completedBar = itemView.findViewById(R.id.completed_progress);
            goalimage = itemView.findViewById(R.id.goalpic);
        }
    }
}



