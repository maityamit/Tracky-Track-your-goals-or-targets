package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    PinView pinView;
    AppCompatButton verify;
    String phonenumber,email,name;
    String otpid;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        pinView = findViewById(R.id.pinView);
        verify = findViewById(R.id.verifyotp);
        mAuth = FirebaseAuth.getInstance();
        phonenumber = getIntent().getStringExtra("mobile").toString();
        email = getIntent().getStringExtra("email").toString();
        name = getIntent().getStringExtra("name").toString();

        initiateOTP();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinView.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter the OTP", Toast.LENGTH_SHORT).show();
                }else if (pinView.getText().toString().length()!=6){
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                }else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid,pinView.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }

    private void initiateOTP() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                40,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid=s;
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                             HashMap<String, Object> hashMap = new HashMap<>();
                             hashMap.put("name",name);
                             hashMap.put("email",email);
                            String currentuserid = mAuth.getCurrentUser().getUid().toString();
                            DatabaseReference Rootref = FirebaseDatabase.getInstance().getReference().child("Users");
                            Rootref.child(currentuserid).updateChildren(hashMap);
                            startActivity(new Intent(VerifyOTP.this,HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(VerifyOTP.this, "Failed to Login!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}