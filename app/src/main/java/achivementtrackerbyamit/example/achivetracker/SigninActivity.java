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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email,pass;
    TextView frgtpass,gotosignup;
    Button signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.loginemail);
        pass = findViewById(R.id.loginpassword);
        frgtpass = findViewById(R.id.frgtpass);
        signin = findViewById(R.id.signin);
        gotosignup = findViewById(R.id.signuptext);
        ProgressBar progressBar = findViewById(R.id.progressBar2);

        frgtpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SigninActivity.this, ForgetpassActivity.class);
                startActivity(intent1);
            }
        });
        gotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(SigninActivity.this,SignupActivity.class);
                startActivity(intent2);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtext = email.getText().toString().trim();
                String passtext = pass.getText().toString().trim();
                if (emailtext.isEmpty()){
                    email.setError("Field can't be empty");
                    email.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailtext).matches()){
                    email.setError("Please enter a valid Email id");
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
                else {
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(emailtext,passtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user.isEmailVerified()){
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent2 = new Intent(SigninActivity.this,HomeActivity.class);
                                    startActivity(intent2);
                                    finishAffinity();
                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    user.sendEmailVerification();
                                    Toast.makeText(SigninActivity.this, "Check your email to verify your account and Login again", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SigninActivity.this, "Failed to Login! Please check your credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}