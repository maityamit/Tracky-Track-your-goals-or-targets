package achivementtrackerbyamit.example.achivetracker.archive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    }

    @Override
    public int getItemCount() {
        return archiveDataList.size();
    }

    public static class ArchiveViewHolder extends RecyclerView.ViewHolder{

        private TextView goal_name_text, consistency_text,target_date_text;
        private ProgressBar consistencyBar;

        public ArchiveViewHolder(@NonNull View itemView) {
            super(itemView);

            goal_name_text= itemView.findViewById(R.id.archieve_goal_name);
            consistency_text= itemView.findViewById(R.id.archieve_goal_const);
            target_date_text= itemView.findViewById(R.id.archieve_target_date);
            consistencyBar = itemView.findViewById(R.id.consistency_progress);
        }
    }
}
