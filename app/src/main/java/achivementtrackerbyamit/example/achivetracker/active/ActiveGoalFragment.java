package achivementtrackerbyamit.example.achivetracker.active;

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

import achivementtrackerbyamit.example.achivetracker.AddGoalActivity;
import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.archive.ArchiveClass;


public class ActiveGoalFragment extends Fragment {



    RecyclerView recyclerView;
    String currentUserID;
    DatabaseReference RootRef,archiveDataRef,activityRef, userRef, RootRefUpdate;
    public static int maxId = 0;
    SimpleArcLoader mDialog;
    ExtendedFloatingActionButton button;
    long count =0, sum=0;
    // Used for carrying out goal search using the GoalAdapter
    GoalAdapter goalAdapter;
    EditText goalSearch;
    ArrayList<Pair<String,GoingCLass>> goalList = new ArrayList<>();
    ImageView emptyGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_active_goal, container, false);



        mDialog = view.findViewById(R.id.loader_active_goal);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");
        archiveDataRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Archive_Goals");
        activityRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Activity");
        recyclerView = view.findViewById(R.id.going_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // ImageView for displaying the empty goal message
        emptyGoal = (ImageView) view.findViewById(R.id.empty_goal_img);
        // Goal and NoResult EditText Views
        goalSearch = (EditText) view.findViewById(R.id.goal_search);
        TextView noResultText = (TextView) view.findViewById(R.id.no_result);
        button = view.findViewById(R.id.create_button);
        userRef = FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Average");
        RootRefUpdate= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID);



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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnNewGoalCreatefn();
            }
        });

        getAvg();


        return  view;
    }

    private void getAvg() {

        SimpleDateFormat formatt = new SimpleDateFormat("dd/M/yyyy");

        RootRefUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Date today = new Date();
                String td = formatt.format(today);
                if(!snapshot.hasChild("Average")) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Average/String", "00;00;00;00;00;00;00");
                    map.put("Average/PDate", td);
                    RootRefUpdate.updateChildren ( map );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = snapshot.getChildrenCount();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GoingCLass goal = dataSnapshot.getValue(GoingCLass.class);
                    sum+= Integer.parseInt(goal.getConsistency());
                }


                if(count!=0) {
                    int avg = (int) (sum/count);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            SimpleDateFormat newFormat = new SimpleDateFormat("dd/M/yyyy");
                            String s = snapshot.child("String").getValue().toString();
                            String d = snapshot.child("PDate").getValue().toString();
                            String[] fin  = new String[7];
                            String[] sp = s.split(";");
                            Date date = new Date();
                            String newd= newFormat.format(date);
                            Date ndate, pdate;
                            try {
                                ndate = newFormat.parse(newd);
                                pdate = newFormat.parse(d);

                                long xf = ndate.getTime() - pdate.getTime();
                                long s_econdsInMilli = 1000;
                                long m_inutesInMilli = s_econdsInMilli * 60;
                                long h_oursInMilli = m_inutesInMilli * 60;
                                long d_aysInMilli = h_oursInMilli * 24;

                                long diff = xf / d_aysInMilli;
                                if(diff > 0) {
                                    if(diff < 7) {
                                        int i=(int) diff, j=0;
                                        int x = (int)diff - 1, y=0;
                                        while(i<7) {
                                            fin[j] = sp[i];
                                            i++;
                                            j++;
                                        }
                                        while(y<x){
                                            fin[j] = sp[i-1];
                                            j++;
                                            y++;
                                        }
                                        fin[fin.length-1] = String.valueOf(avg);
                                    } else {
                                        for(int i=0; i<sp.length-1; i++) {
                                            fin[i] = "00";
                                        }
                                        fin[sp.length-1] = String.valueOf(avg);
                                    }
                                    String up = "";
                                    for(int i=0; i<6; i++) {
                                        up+=fin[i]+";";
                                    }
                                    up+=fin[fin.length-1];
                                    //Toast.makeText(getContext(), up, Toast.LENGTH_SHORT).show();

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("Average/String", up);
                                    map.put("Average/PDate", newd);
                                    RootRefUpdate.updateChildren ( map );

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
    }

    private void OnNewGoalCreatefn() {
        Intent intent = new Intent(getContext(), AddGoalActivity.class);
        startActivity(intent);
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
        goalAdapter = new GoalAdapter(this,goalList);
        recyclerView.setAdapter ( goalAdapter );
        goalSearch.setText("");
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