package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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

public class MainActivity extends AppCompatActivity {


    ExtendedFloatingActionButton button;
    RecyclerView recyclerView;
    String currentUserID;
    DatabaseReference RootRef;
    ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.create_button);

        logout = findViewById(R.id.logout_btn);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        RootRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Goals").child("Active");

        recyclerView = findViewById(R.id.going_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddtripActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);
                builder.setMessage("Are you sure,you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                Intent loginIntenttt = new Intent ( MainActivity.this,SplasshActivity.class );
                                loginIntenttt.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity ( loginIntenttt );
                                finish ();

                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(),"Thank you for Staying here",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
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

                        String listPostKey = getRef(position).getKey();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-M-yyyy");

                        Date today = new Date();
                        String todaay = simpleDateFormat.format(today);
                        String jys_da = simpleDateFormat2.format(today);

                        holder.goal_name.setText(model.getGoalName());
                        holder.goal_priority.setText(model.getGoalType());
                        holder.const_text.setText("Consistency :" +model.getConsistency()+" %");


                        RootRef.child(listPostKey).child("Win").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.hasChild(jys_da)) {
                                    holder.checkBox_true.setVisibility(View.INVISIBLE);
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

                        holder.left_day.setText(elapsedDays+" days"+"\nleft");



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this,DashboardActivity.class);
                                intent.putExtra("LISTKEY",listPostKey);
                                startActivity(intent);
                            }
                        });

                        holder.checkBox_true.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                        {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                            {
                                if ( isChecked )
                                {
                                    // perform logic
                                    HashMap<String,Object> onlineStat = new HashMap<> (  );
                                    onlineStat.put ( "Value", "true");

                                    RootRef.child(listPostKey).child("Win").child(jys_da)
                                            .updateChildren ( onlineStat );





                                    holder.checkBox_true.setVisibility(View.INVISIBLE);
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