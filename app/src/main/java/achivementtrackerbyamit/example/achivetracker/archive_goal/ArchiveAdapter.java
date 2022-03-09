package achivementtrackerbyamit.example.achivetracker.archive_goal;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import achivementtrackerbyamit.example.achivetracker.R;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>{

    private Context context;
    private ArrayList<DataSnapshot> archiveDataList;

    public ArchiveAdapter(Context context, ArrayList<DataSnapshot> archiveDataList) {
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

        DataSnapshot snapshot = archiveDataList.get(position);
        ArchiveClass data= snapshot.getValue(ArchiveClass.class);

        String firstWord= data.getEndTime().substring(0, data.getEndTime().indexOf(" "));

        holder.goal_name_text.setText(data.getGoalName());
        holder.target_date_text.setText(firstWord);
        holder.consistency_text.setText("Consistency :" +data.getConsistency()+" %");
        holder.switchCompat.setOnCheckedChangeListener(null);

        // Set consistency percentage on the progress bar
        holder.consistencyBar.setProgress(Integer.parseInt(data.getConsistency()),true);

        // Check the switch if auto delete is on
        if(data.isAutoDelete())
        {
            holder.switchCompat.setChecked(true);
        }
        
        holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

               // When there is a change in the value of auto delete, then change it in firebase accordingly
               if(data.isAutoDelete()!=holder.switchCompat.isChecked())
               {
                   // Set value of auto delete according to the switch
                   data.setAutoDelete(holder.switchCompat.isChecked());

                   // If auto delete is set to true, set the current date as the delete date that will be used as a reference to check when the goal will be deleted
                   if(data.isAutoDelete())
                   {
                       Toast.makeText(context, "This goal will be deleted after 7 days", Toast.LENGTH_SHORT).show();
                       Calendar calendar = Calendar.getInstance();
                       String currentDate = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
                       data.setDeleteDate(currentDate);
                   }

                   // Update the goal in firebase
                   snapshot.getRef().setValue(data);
               }
           }
        });


    }


    public void setArchiveDataList(ArrayList<DataSnapshot> archiveList) {

        // DiffUtils used in place of notifyDataSetChanged to ensure that only those items are replaced which have been changed
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return archiveDataList.size();
            }

            @Override
            public int getNewListSize() {
                return archiveList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return Objects.equals(archiveList.get(newItemPosition).getKey(), archiveDataList.get(oldItemPosition).getKey());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return areItemsTheSame(oldItemPosition,newItemPosition);
            }
        });
        this.archiveDataList = archiveList;
        diffResult.dispatchUpdatesTo(this);
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