package achivementtrackerbyamit.example.achivetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class GoingFragment extends Fragment {

    RecyclerView recyclerView;
    String currentUserID;
    DatabaseReference RootRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  =  inflater.inflate(R.layout.fragment_going, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");

        recyclerView = view.findViewById(R.id.going_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return  view;
    }


    @Override
    public void onStart() {
        super.onStart ();

        FirebaseRecyclerOptions<GoingCLass> options =
                new FirebaseRecyclerOptions.Builder<GoingCLass> ()
                        .setQuery ( RootRef,GoingCLass.class )
                        .build ();


        FirebaseRecyclerAdapter<GoingCLass, StudentViewHolder2> adapter =
                new FirebaseRecyclerAdapter<GoingCLass, StudentViewHolder2> (options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final StudentViewHolder2 holder, final int position, @NonNull final GoingCLass model) {


                        holder.goal_name.setText(model.getGoalName());
                        holder.goal_priority.setText(model.getGoalType());
                        holder.const_text.setText(model.getConsistency());


                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

                        Date today = new Date();
                        String todaay = simpleDateFormat.format(today);

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

                        long elapsedHours = different / hoursInMilli;
                        different = different % hoursInMilli;

                        long elapsedMinutes = different / minutesInMilli;
                        different = different % minutesInMilli;

                        long elapsedSeconds = different / secondsInMilli;

                        holder.left_day.setText(elapsedDays+"\nleft");

                        String listPostKey = getRef(position).getKey();


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(),DashboardActivity.class);
                                intent.putExtra("LISTKEY",listPostKey);
                                startActivity(intent);
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



                    }


                };
        recyclerView.setAdapter ( adapter );
        adapter.startListening ();




    }

    public static class StudentViewHolder2 extends  RecyclerView.ViewHolder
    {

        TextView goal_name,goal_priority,left_day,const_text;
        CheckBox checkBox_true;
        public StudentViewHolder2(@NonNull View itemView) {
            super ( itemView );
            goal_name = itemView.findViewById ( R.id.lay_goal_name);
            goal_priority = itemView.findViewById ( R.id.lay_goal_priority);
            left_day = itemView.findViewById ( R.id.lay_goal_left);
            const_text = itemView.findViewById ( R.id.lay_goal_const);
            checkBox_true = itemView.findViewById ( R.id.true_checkbox);
        }
    }
}