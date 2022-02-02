package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    TextView welcome1,welcome2;
    private DatabaseReference reference;
    private String userID;
    AppCompatButton Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        welcome1 = findViewById(R.id.users_name);
        welcome2 = findViewById(R.id.users_email);
        Logout = findViewById(R.id.logout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, SignupActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class);

                if (userprofile != null){
                    String fullname = userprofile.name;
                    String email = userprofile.email;

                    welcome1.setText(fullname);
                    welcome2.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}