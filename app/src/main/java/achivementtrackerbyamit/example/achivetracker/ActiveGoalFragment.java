package achivementtrackerbyamit.example.achivetracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class ActiveGoalFragment extends Fragment {



    RecyclerView recyclerView;
    String currentUserID;
    DatabaseReference RootRef;
    ProgressDialog progressDialog;
    public static int confirmation = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_active_goal, container, false);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");

        recyclerView = view.findViewById(R.id.going_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return  view;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_diaglog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//        Runnable progressRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (confirmation != 1) {
//                    progressDialog.cancel();
//                    Toast.makeText(MainActivity.this, "Fetching data from Firebase", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//
//        Handler pdCanceller = new Handler();
//        pdCanceller.postDelayed(progressRunnable, 5000);
    }


    @Override
    public void onStart() {
        super.onStart ();

        showProgressDialog();

        FirebaseRecyclerOptions<GoingCLass> options =
                new FirebaseRecyclerOptions.Builder<GoingCLass> ()
                        .setQuery ( RootRef,GoingCLass.class )
                        .build ();


        FirebaseRecyclerAdapter<GoingCLass, StudentViewHolder2> adapter =
                new FirebaseRecyclerAdapter<GoingCLass, StudentViewHolder2> (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final StudentViewHolder2 holder, final int position, @NonNull final GoingCLass model) {

                        String listPostKey = getRef(position).getKey();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-M-yyyy");

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


                        RootRef.child(listPostKey).child("Win").addListenerForSingleValueEvent(new ValueEventListener() {
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

                        if (elapsedDays==1){
                            holder.left_day.setText(elapsedDays+" day"+"\nleft");
                        }
                        else {
                            holder.left_day.setText(elapsedDays + " days" + "\nleft");
                        }



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(),DashboardActivity.class);
                                intent.putExtra("LISTKEY",listPostKey);
                                startActivity(intent);
                            }
                        });

                        holder.checkBox_true.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                        {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    //Alert Dialog Box Added
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Confirm Goal Completion?");
                                    builder.setTitle("Alert !");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) { //For True
                                            // perform logic
                                            HashMap<String, Object> onlineStat = new HashMap<>();
                                            onlineStat.put("Value", "true");
                                            RootRef.child(listPostKey).child("Win").child(jys_da)
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
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                } else {
                                    //Fail
                                    Toast.makeText(getContext(), "Not Checked", Toast.LENGTH_SHORT).show(); //Just to Inform the user
                                }
                            }
                        });




                    }

                    @NonNull
                    @Override
                    public StudentViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view  = LayoutInflater.from ( viewGroup.getContext () ).inflate ( R.layout.main_tiles_layout,viewGroup,false );
                        StudentViewHolder2 viewHolder  = new StudentViewHolder2(  view);
                        return viewHolder;

                    }

                    @Override
                    public void onDataChanged() {
                        super.onDataChanged();

                        progressDialog.dismiss();


                    }


                };
        recyclerView.setAdapter ( adapter );
        adapter.startListening ();




    }

    public static class StudentViewHolder2 extends  RecyclerView.ViewHolder
    {

        TextView goal_name,goal_priority,left_day,const_text;
        RelativeLayout goal_type_layout;
        LinearLayout check_in_layout;
        CheckBox checkBox_true;
        public StudentViewHolder2(@NonNull View itemView) {
            super ( itemView );
            goal_name = itemView.findViewById ( R.id.lay_goal_name);
            goal_priority = itemView.findViewById ( R.id.lay_goal_priority);
            left_day = itemView.findViewById ( R.id.lay_goal_left);
            goal_type_layout = itemView.findViewById(R.id.goal_type_layout);

            check_in_layout = itemView.findViewById(R.id.check_in_layout);
            const_text = itemView.findViewById ( R.id.lay_goal_const);
            checkBox_true = itemView.findViewById ( R.id.true_checkbox);
        }
    }

}