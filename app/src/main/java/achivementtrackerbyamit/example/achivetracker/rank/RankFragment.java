package achivementtrackerbyamit.example.achivetracker.rank;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leo.simplearcloader.SimpleArcLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import achivementtrackerbyamit.example.achivetracker.R;


public class RankFragment extends Fragment {

    RankAdapter rankAdapter;
    RecyclerView rankList;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    TextView firstName;
    TextView firstGoal;
    ImageView firstImage;
    TextView firstConsistency;
    LinearLayout firstLayout;

    TextView secondName;
    TextView secondGoal;
    ImageView secondImage;
    TextView secondConsistency;
    LinearLayout secondLayout;

    TextView thirdName;
    TextView thirdGoal;
    ImageView thirdImage;
    TextView thirdConsistency;
    LinearLayout thirdLayout;

    SimpleArcLoader dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rank, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Topper");
        auth = FirebaseAuth.getInstance();

        // First ranked
        firstName = view.findViewById(R.id.first_rank_name);
        firstGoal = view.findViewById(R.id.first_rank_goal_name);
        firstImage = view.findViewById(R.id.first_rank_pfp);
        firstConsistency = view.findViewById(R.id.first_rank_consistency);
        firstLayout = view.findViewById(R.id.first_rank_layout);

        // Second ranked
        secondName = view.findViewById(R.id.second_rank_name);
        secondGoal = view.findViewById(R.id.second_rank_goal_name);
        secondImage = view.findViewById(R.id.second_rank_pfp);
        secondConsistency = view.findViewById(R.id.second_rank_consistency);
        secondLayout = view.findViewById(R.id.second_rank_layout);

        // Third ranked
        thirdName = view.findViewById(R.id.third_rank_name);
        thirdGoal = view.findViewById(R.id.third_rank_goal_name);
        thirdImage = view.findViewById(R.id.third_rank_pfp);
        thirdConsistency = view.findViewById(R.id.third_rank_consistency);
        thirdLayout = view.findViewById(R.id.third_rank_layout);

        // Rest of the goals list
        rankList = view.findViewById(R.id.rank_list);

        dialog = view.findViewById(R.id.loader_rank);


        RetriveDataMethods();

        return  view;
    }

    private void RetriveDataMethods() {

        // Getting list of best goals
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dialog.setVisibility(View.GONE);

                // Sorting the list based on consistency
                List<DataSnapshot> ranks = Lists.newArrayList(snapshot.getChildren());
                ranks.sort(new Comparator<DataSnapshot>() {
                    @Override
                    public int compare(DataSnapshot d1, DataSnapshot d2) {
                        return Integer.parseInt(d2.child("consistency").getValue(String.class).toString())-Integer.parseInt(d1.child("consistency").getValue(String.class).toString());
                    }
                });

                // Getting goals other than the top 3
                List<DataSnapshot> remainingRanks = new ArrayList<>();
                for(int i=3;i<ranks.size();i++) remainingRanks.add(ranks.get(i));

                // Displaying first ranked goal
                if(ranks.size()>0)
                {
                    DataSnapshot dataSnapshot = ranks.get(0);


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    String key = dataSnapshot.getKey();

                    reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshott) {

                            if (snapshott.hasChild("name")){
                                String name = snapshott.child("name").getValue().toString();
                                if(FirebaseAuth.getInstance().getUid().equals(key)) name="Me";
                                firstName.setText(name);
                            }

                            if (snapshott.hasChild("user_image")){
                                Picasso.get().load(snapshott.child("user_image").getValue().toString()).error(R.drawable.profile).placeholder(R.drawable.profile).into(firstImage);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Cannot fetch data", Toast.LENGTH_SHORT).show();
                        }
                    });


                    firstGoal.setText(dataSnapshot.child("goal_Name").getValue(String.class));
                    int consistency = Integer.parseInt(dataSnapshot.child("consistency").getValue(String.class));
                    if(consistency>=0) firstConsistency.setText(consistency+"%");
                    firstLayout.setVisibility(View.VISIBLE);
                }
                else firstLayout.setVisibility(View.GONE);

                // Displaying second ranked goal
                if(ranks.size()>1)
                {
                    DataSnapshot dataSnapshot = ranks.get(1);


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    String key = dataSnapshot.getKey();

                    reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshott) {

                            if (snapshott.hasChild("name")){
                                String name = snapshott.child("name").getValue().toString();
                                if(FirebaseAuth.getInstance().getUid().equals(key)) name="Me";
                                secondName.setText(name);
                            }

                            if (snapshott.hasChild("user_image")){
                                Picasso.get().load(snapshott.child("user_image").getValue().toString()).error(R.drawable.profile).placeholder(R.drawable.profile).into(secondImage);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Cannot fetch data", Toast.LENGTH_SHORT).show();
                        }
                    });



                    secondGoal.setText(dataSnapshot.child("goal_Name").getValue(String.class));
                    int consistency = Integer.parseInt(dataSnapshot.child("consistency").getValue(String.class));
                    if(consistency>=0) secondConsistency.setText(consistency+"%");
                    secondLayout.setVisibility(View.VISIBLE);
                }

                else secondLayout.setVisibility(View.GONE);

                // Displaying third ranked goal
                if(ranks.size()>2)
                {
                    DataSnapshot dataSnapshot = ranks.get(2);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    String key = dataSnapshot.getKey();

                    reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshott) {

                            if (snapshott.hasChild("name")){
                                String name = snapshott.child("name").getValue().toString();
                                if(FirebaseAuth.getInstance().getUid().equals(key)) name="Me";
                                thirdName.setText(name);
                            }

                            if (snapshott.hasChild("user_image")){
                                Picasso.get().load(snapshott.child("user_image").getValue().toString()).error(R.drawable.profile).placeholder(R.drawable.profile).into(thirdImage);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Cannot fetch data", Toast.LENGTH_SHORT).show();
                        }
                    });

                    thirdGoal.setText(dataSnapshot.child("goal_Name").getValue(String.class));
                    int consistency = Integer.parseInt(dataSnapshot.child("consistency").getValue(String.class));
                    if(consistency>=0) thirdConsistency.setText(consistency+"%");
                    thirdLayout.setVisibility(View.VISIBLE);
                }
                else thirdLayout.setVisibility(View.GONE);

                // Displaying rest of the goals in recycler view
                if(getContext()!=null)
                {
                    rankAdapter = new RankAdapter(getContext(),remainingRanks);
                    rankList.setAdapter(rankAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}