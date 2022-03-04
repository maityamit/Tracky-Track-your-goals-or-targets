package achivementtrackerbyamit.example.achivetracker.archive_goal;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import achivementtrackerbyamit.example.achivetracker.R;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>{

    private Context context;
    private ArrayList<ArchiveClass> archiveDataList;


    public ArchiveAdapter(Context context, ArrayList<ArchiveClass> archiveDataList) {
        this.context = context;
        this.archiveDataList = archiveDataList;
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.archive_goals_item_layout,parent,false);
        return new ArchiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder holder, int position) {

        ArchiveClass data= archiveDataList.get(position);

        String firstWord= data.getEndTime().substring(0, data.getEndTime().indexOf(" "));

        holder.goal_name_text.setText(data.getGoalName());
        holder.target_date_text.setText(firstWord);
        holder.consistency_text.setText("Consistency :" +data.getConsistency()+" %");

        // Set consistency percentage on the progress bar
        holder.consistencyBar.setProgress(Integer.parseInt(data.getConsistency()),true);

       if (holder.switchCompat.isChecked()){
           // Function implement
       }




    }

    @Override
    public int getItemCount() {
        return archiveDataList.size();
    }

    public static class ArchiveViewHolder extends RecyclerView.ViewHolder{

        private TextView goal_name_text, consistency_text,target_date_text;
        private ProgressBar consistencyBar;
        private SwitchCompat switchCompat;

        public ArchiveViewHolder(@NonNull View itemView) {
            super(itemView);

            goal_name_text= itemView.findViewById(R.id.archieve_goal_name);
            consistency_text= itemView.findViewById(R.id.archieve_goal_const);
            target_date_text= itemView.findViewById(R.id.archieve_target_date);
            consistencyBar = itemView.findViewById(R.id.consistency_progress);
            switchCompat = itemView.findViewById(R.id.auto_delete);
        }
    }
}