package achivementtrackerbyamit.example.achivetracker;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

import achivementtrackerbyamit.example.achivetracker.archive.ArchiveClass;


public class ActiveGoalFragment extends Fragment {



    RecyclerView recyclerView;
    String currentUserID;
    DatabaseReference RootRef,archiveDataRef;
    public static int maxId = 0;
    SimpleArcLoader mDialog;
    FirebaseRecyclerAdapter<GoingCLass, StudentViewHolder2> adapter;

    // Used for carrying out goal search using the GoalAdapter
    GoalAdapter goalAdapter;
    EditText goalSearch;
    ArrayList<Pair<String,GoingCLass>> goalList = new ArrayList<>();

    ImageView emptyGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_active_goal, container, false);

        mDialog = view.findViewById(R.id.loader_active_goal);

       




        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");
        // set data base reference for archieve data
        archiveDataRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Archive_Goals");

        recyclerView = view.findViewById(R.id.going_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ImageView for displaying the empty goal message
        emptyGoal = (ImageView) view.findViewById(R.id.empty_goal_img);

        // Goal and NoResult EditText Views
        goalSearch = (EditText) view.findViewById(R.id.goal_search);
        TextView noResultText = (TextView) view.findViewById(R.id.no_result);

        // Carrying out search when text is added to the goalSearch
        goalSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                // Creating new list of goals based on the entered value in the goalSearch
                ArrayList<Pair<String,GoingCLass>> newList = new ArrayList<>();
                for(int i=0;i<goalList.size();i++)
                {
                    GoingCLass item = goalList.get(i).second;

                    // Checking if the entered text matches with the goal name
                    if(item.getGoalName().toLowerCase().contains(editable.toString().toLowerCase()))
                    {
                        newList.add(goalList.get(i));
                    }
                }
                goalAdapter.setGoalList(newList);

                // Making the no result text visible based on the size of the new list
                if(!editable.toString().isEmpty() && newList.size()==0 && !(goalList.size() ==0))
                {
                    noResultText.setVisibility(View.VISIBLE);
                }
                else
                {
                    noResultText.setVisibility(View.GONE);
                }
            }
        });
        return  view;
    }



    @Override
    public void onStart() {
        super.onStart ();


        mDialog.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        FirebaseRecyclerOptions<GoingCLass> options =
                new FirebaseRecyclerOptions.Builder<GoingCLass> ()
                        .setQuery ( RootRef,GoingCLass.class )
                        .build ();

        // Old FirebaseRecyclerAdapter
        adapter = new FirebaseRecyclerAdapter<GoingCLass, StudentViewHolder2>  (options) {

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

                        // TODO: implemented (Complete)

                         if(different<0){

                             postDataIntoArchive();
                             ArchiveClass goal= new ArchiveClass(model.getConsistency(), model.getEndTime(), model.getGoalName());

                            archiveDataRef.child(String.valueOf(maxId+1)).setValue(goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // Delete the data from current fragment
                                    deleteArchieveData(listPostKey);
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
                                    //Material Alert Dialog Box Added
                                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(),R.style.AlertDialogTheme1);
                                    builder.setTitle("Alert!");
                                    builder.setMessage("Confirm Goal Completion?");
                                    builder.setBackground(getResources().getDrawable(R.drawable.material_dialog_box , null));
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
                                    builder.show();
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

                        mDialog.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }



                };

        // Getting the list of goals
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Getting the latest list of goals and updating the goalList
                ArrayList<Pair<String,GoingCLass>> currList = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    GoingCLass goal = dataSnapshot.getValue(GoingCLass.class);
                    currList.add(new Pair<>(dataSnapshot.getKey(),goal ));
                }
                goalList=currList;

                // Updating the adapter with the new goal list
                goalAdapter.setGoalList(goalList);

                // Making the recycler view appear and the loading dialog disappear
                mDialog.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                // Making the empty goal message visible when goal list is empty
                if(goalList.size()==0) emptyGoal.setVisibility(View.VISIBLE);
                else emptyGoal.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Setting the old adapter, old adapter can be used by uncommenting these lines and commenting the lines below them
//        recyclerView.setAdapter ( adapter );
//        adapter.startListening ();

        // Setting the new custom adapter
        goalAdapter = new GoalAdapter(this,goalList);
        recyclerView.setAdapter ( goalAdapter );
        goalSearch.setText("");
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

    public void postDataIntoArchive() {

        archiveDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxId= (int)snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteArchieveData(String listPostKey) {
        // remove the data from current fragment
        RootRef.child(listPostKey).removeValue();

    }

}