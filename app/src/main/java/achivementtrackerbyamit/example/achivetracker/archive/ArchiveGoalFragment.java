package achivementtrackerbyamit.example.achivetracker.archive;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leo.simplearcloader.SimpleArcLoader;

import java.util.ArrayList;
import java.util.Objects;

import achivementtrackerbyamit.example.achivetracker.R;

public class ArchiveGoalFragment extends Fragment {

    RecyclerView recyclerView;
    String currentUserID;
    DatabaseReference archiveDataRef;
    ArchiveAdapter archiveAdapter;
    ArrayList<ArchiveClass> dataList;
    SimpleArcLoader mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_archive_goal, container, false);

        mDialog = view.findViewById(R.id.loader_archive_goal);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        archiveDataRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Archive_Goals");

        recyclerView = view.findViewById(R.id.archieve_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        mDialog.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        dataList= new ArrayList<>();
        archiveAdapter= new ArchiveAdapter(getContext(),dataList);
        recyclerView.setAdapter(archiveAdapter);

        archiveDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()){
                    mDialog.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                //fetch all the data
                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                        ArchiveClass itemData= dataSnapshot.getValue(ArchiveClass.class);
                        dataList.add(itemData);
                    }
                    archiveAdapter.notifyDataSetChanged();
                    mDialog.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    /*    FirebaseRecyclerOptions<ArchiveCLass> options =
                new FirebaseRecyclerOptions.Builder<ArchiveCLass> ()
                        .setQuery ( FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Archive_Goals"),ArchiveCLass.class )
                        .build ();


          adapter = new FirebaseRecyclerAdapter<ArchiveCLass, ArchiveGoalFragment.StudentViewHolder3> (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ArchiveGoalFragment.StudentViewHolder3 holder, final int position, @NonNull final ArchiveCLass model) {

                        String listPostKey = getRef(position).getKey();

                        //SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-M-yyyy");

                        holder.goal_name.setText(model.getGoalName());
                        holder.goal_consistency.setText("Consistency :" +model.getConsistency()+" %");
                        //Date date= model.getEndTime();
                        holder.target_date.setText(model.getEndTime());

                    }

                    @NonNull
                    @Override
                    public StudentViewHolder3 onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view  = LayoutInflater.from ( viewGroup.getContext () ).inflate ( R.layout.archive_goals_item_layout,viewGroup,false );
                        StudentViewHolder3 viewHolder  = new StudentViewHolder3(  view);
                        return viewHolder;

                    }

                    @Override
                    public void onDataChanged() {
                        super.onDataChanged();

                        progressDialog.dismiss();

                    }

                };

        recyclerView.setAdapter ( adapter );
        recyclerView.setHasFixedSize(false);
        adapter.startListening ();*/


    }


    /*public class StudentViewHolder3 extends  RecyclerView.ViewHolder
    {

        TextView goal_name,goal_consistency,target_date;
        public StudentViewHolder3(@NonNull View itemView) {
            super ( itemView );
            goal_name = itemView.findViewById ( R.id.archieve_goal_name);
            goal_consistency= itemView.findViewById(R.id.archieve_goal_const);
            target_date= itemView.findViewById(R.id.archieve_target_date);
        }
    }*/


}