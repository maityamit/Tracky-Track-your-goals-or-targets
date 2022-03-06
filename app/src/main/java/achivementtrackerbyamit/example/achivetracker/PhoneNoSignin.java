package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneNoSignin extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText name,phone,email;
    AppCompatButton getotp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no_signin);

        countryCodePicker = findViewById(R.id.countrycodepicker);
        name = findViewById(R.id.username);
        phone = findViewById(R.id.phoneno);
        email = findViewById(R.id.email);
        getotp = findViewById(R.id.getotp);
        countryCodePicker.registerCarrierNumberEditText(phone);

        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = name.getText().toString();
                String emailString= email.getText().toString();
                String phoneString = phone.getText().toString();
                if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(emailString) && TextUtils.isEmpty(phoneString)){
                    Toast.makeText(PhoneNoSignin.this, "Please enter the details!", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(PhoneNoSignin.this,VerifyOTP.class);
                    intent.putExtra("mobile",countryCodePicker.getFullNumberWithPlus().replace(" ",""));
                    intent.putExtra("name",nameString);
                    intent.putExtra("email",emailString);
                    startActivity(intent);
                }
            }
        });
    }
}