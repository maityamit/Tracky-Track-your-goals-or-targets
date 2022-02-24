package achivementtrackerbyamit.example.achivetracker;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
    DatabaseReference RootRef,archiveDataRef,activityRef;
    public static int maxId = 0;
    SimpleArcLoader mDialog;
    FirebaseRecyclerAdapter<GoingCLass, StudentViewHolder2> adapter;
    ExtendedFloatingActionButton button;

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
        activityRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Activity");

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

                if(!newList.isEmpty()){
                    goalAdapter.setGoalList(newList);

                }


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

        // Floating action button for new goals
        button = view.findViewById(R.id.create_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Add new Goals", Toast.LENGTH_SHORT).show(); //Informs user that what this button does
                Intent intent = new Intent(getContext(), AddGoalActivity.class);
                startActivity(intent);
            }
        });
        return  view;
    }



    @Override
    public void onStart() {
        super.onStart ();


        mDialog.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

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
                goalList = currList;

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
        LinearLayout  linearLayout;
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
                    maxId = (int)snapshot.getChildrenCount();
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