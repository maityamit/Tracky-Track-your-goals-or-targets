package achivementtrackerbyamit.example.achivetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import achivementtrackerbyamit.example.achivetracker.auth.Users;
import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment {




    private TextView username , rateus , share , privacypolicy;
    private Button showLogs;
    private DatabaseReference reference;
    private String userID;
    CircleImageView profilePic;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilePic = view.findViewById(R.id.profile_pic);
        rateus = view.findViewById(R.id.rateus);
        share = view.findViewById(R.id.share);
        privacypolicy = view.findViewById(R.id.privacy);
        username = view.findViewById(R.id.users_name);
        showLogs = view.findViewById(R.id.show_logs);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users");


        // Fetching data from firebase and displaying in the SettingsFragment
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if(users!=null){
                    String name = users.name;

                    username.setText(name);
                }
                Object url = snapshot.child("user_image").getValue();
                if (url!=null){
                    Picasso.get().load(url.toString()).placeholder(R.drawable.profile).error(R.drawable.profile).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Cannot Fetch Data", Toast.LENGTH_SHORT).show();
            }
        });

        // Show logs button onClickListener
        showLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the list of activities
                reference.child(mAuth.getUid()).child("Activity").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List<DataSnapshot> logs = Lists.newArrayList(task.getResult().getChildren());

                            // Adding the list of activities to an array adapter
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.show_log_dialog);
                            for(int i=logs.size()-1;i>=0;i--)
                            {
                                adapter.add(logs.get(i).getValue(String.class));
                            }

                            // Creating alert dialog for showing logs
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                    .setTitle("Logs")
                                    .setAdapter(adapter,null)
                                    .setNegativeButton("Close",null)
                                    .create();
                            if(logs.size()==0) alertDialog.setMessage("No recent activity");
                            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.material_dialog_box);
                            alertDialog.show();
                        }
                    }
                });

            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    // Function for github link view
    public void GithubLinkClick(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/Tracky-Track-your-goals-or-targets"));
        startActivity(browserIntent);

    }

}