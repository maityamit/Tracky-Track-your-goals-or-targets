package achivementtrackerbyamit.example.achivetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    ExtendedFloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.create_button);

        chipNavigationBar = findViewById(R.id.chip_mni);
        chipNavigationBar.setItemSelected(R.id.current, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GoingFragment()).commit();

//        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int i) {
//                Fragment fragment = null;
//                switch (i) {
//                    case R.id.home:
//                        fragment = new GoingFragment();
//                        break;
//                    case R.id.past:
//                        fragment = new PastFragment();
//                        break;
//                    case R.id.settings:
//                        fragment = new SettingsFragment();
//                        break;
//                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
//            }
//        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddtripActivity.class);
                startActivity(intent);
            }
        });


    }
}