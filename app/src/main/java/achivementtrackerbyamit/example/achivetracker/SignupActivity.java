package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText name,email,pass;
    Button signup;
    TextView gotosignin;
    String emailtext,passtext,confpasstext,nametext;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        email = findViewById(R.id.signupemail);
        pass = findViewById(R.id.signuppass);
        signup = findViewById(R.id.signupbtn);
        gotosignin = findViewById(R.id.gotosignin);
        progressBar = findViewById(R.id.progressBar);

        gotosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,SigninActivity.class);
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nametext = name.getText().toString().trim();
                emailtext = email.getText().toString().trim();
                passtext = pass.getText().toString().trim();

                if (nametext.isEmpty()){
                    name.setError("Field can't be empty");
                    name.requestFocus();
                    return;
                }

                if (emailtext.isEmpty()){
                    email.setError("Field can't be empty");
                    email.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailtext).matches()){
                    email.setError("Please enter a valid email address");
                    email.requestFocus();
                    return;
                }
                else if (passtext.isEmpty()){
                    pass.setError("Field can't be empty");
                    pass.requestFocus();
                    return;
                }
                else if (passtext.length()<6){
                    pass.setError("Password must be atleast 6 characters");
                    pass.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(emailtext,passtext)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Users users = new Users(nametext,emailtext);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                Intent intent = new Intent(SignupActivity.this,SigninActivity.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            }
                                            else{
                                                Toast.makeText(SignupActivity.this, "Registration Failed "+task.getException(), Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(SignupActivity.this, "Registration Failed "+task.getException(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
    }
}