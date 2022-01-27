package achivementtrackerbyamit.example.achivetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplasshActivity extends AppCompatActivity {



    private boolean connected = false;

    private FirebaseAuth mAuth;
    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splassh);
        mAuth = FirebaseAuth.getInstance ();


    }



    @Override
    protected void onStart() {


        super.onStart();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;

            FirebaseUser currentUser = mAuth.getCurrentUser ();

            if (currentUser == null)
            {

                SendUserToLoginActivity();
            }
            else
            {

                currentUserID = mAuth.getCurrentUser ().getUid ();


                Intent loginIntentt = new Intent ( SplasshActivity.this,MainActivity.class );
                loginIntentt.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( loginIntentt );
                finish ();

            }


        }
        else{
            connected=false;
            new AlertDialog.Builder(this)
                    .setTitle("You are Offline Dude ! ")
                    .setMessage("Make sure that you are in online Mode.")
                    .setNegativeButton("Ok",null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }


    private void SendUserToLoginActivity() {


        Intent loginIntent = new Intent (SplasshActivity.this,RegisterActivity.class  );
        loginIntent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( loginIntent );
        finish ();
    }


}