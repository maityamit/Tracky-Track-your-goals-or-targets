package achivementtrackerbyamit.example.achivetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment {




    private TextView username , rateus , share , privacypolicy;
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

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}