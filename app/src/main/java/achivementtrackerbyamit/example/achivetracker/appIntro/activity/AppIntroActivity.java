package achivementtrackerbyamit.example.achivetracker.appIntro.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;

import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.SplasshActivity;
import achivementtrackerbyamit.example.achivetracker.appIntro.slider.LastStepFragment;
import achivementtrackerbyamit.example.achivetracker.appIntro.slider.VisulizationFragment;
import achivementtrackerbyamit.example.achivetracker.appIntro.slider.WelcomeFragment;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
        //addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_welcome));
       // addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_visulization));
        //addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.fragment_last_step));
        SharedPreferences preferences= getApplicationContext().getSharedPreferences(LastStepFragment.class.getName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();

        if(preferences!=null){
            boolean checkShared= preferences.getBoolean("Check state",false);
            if(checkShared){
                startActivity(new Intent(AppIntroActivity.this, SplasshActivity.class));
                finish();
            }
        }


    }


}