package achivementtrackerbyamit.example.achivetracker.appIntro.slider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.SplasshActivity;


public class LastStepFragment extends Fragment {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Button doneBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_last_step, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      //  preferences= getActivity().getPreferences(Context.MODE_PRIVATE);
        preferences= getActivity().getSharedPreferences(LastStepFragment.class.getName(),Context.MODE_PRIVATE);
        editor= preferences.edit();
        doneBtn= view.findViewById(R.id.last_step_done_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("Check state",true);
                editor.commit();
                startActivity(new Intent(getContext(), SplasshActivity.class));
                getActivity().finish();
            }
        });
    }
}