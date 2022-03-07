package achivementtrackerbyamit.example.achivetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SignInDone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_done);

        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(1200);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(SignInDone.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };thread.start();
    }
}