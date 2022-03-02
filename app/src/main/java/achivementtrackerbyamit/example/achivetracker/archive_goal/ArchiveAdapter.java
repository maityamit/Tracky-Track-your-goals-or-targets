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

    private ArchiveGoalFragment fragment;
    private ArrayList<ArchiveClass> archiveDataList;


    public ArchiveAdapter(ArchiveGoalFragment fragment, ArrayList<ArchiveClass> archiveDataList) {
        this.fragment = fragment;
        this.archiveDataList = archiveDataList;
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(fragment.getContext()).inflate(R.layout.archive_goals_item_layout,parent,false);
        return new ArchiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder holder, int position) {

        int currPos= position;
       boolean flag= false;
        ArchiveClass data = archiveDataList.get(position);

        fragment.archiveDataRef.child(String.valueOf(currPos+1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild("Status_On")){
                    holder.autodelete.setVisibility(View.INVISIBLE);
                    holder.auto_del_text.setVisibility(View.INVISIBLE);
                    holder.itemCardView.setCardBackgroundColor(Color.parseColor("#9ACD32"));

                    // Auto delete
                    // endtime -> retrive date
                    // to day

                    if(fragment.timeExceed(data.getEndTime())){
                        fragment.deleteData(String.valueOf(currPos+1));
                       // archiveDataList.remove(data);
                    }

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       // String key= archiveDataList.get(position).first;

        if (archiveDataList.contains(data)&& data.getEndTime() != null) {
            String firstWord = data.getEndTime().substring(0, data.getEndTime().indexOf(" "));

            holder.goal_name_text.setText(data.getGoalName());
            holder.target_date_text.setText(firstWord);
            holder.consistency_text.setText("Consistency :" + data.getConsistency() + " %");

            // Set consistency percentage on the progress bar
            holder.consistencyBar.setProgress(Integer.parseInt(data.getConsistency()), true);

            holder.autodelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {

                        fragment.archiveDataRef.child(String.valueOf(currPos+1)).child("Status_On").setValue("true", new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                holder.autodelete.setVisibility(View.INVISIBLE);
                                holder.auto_del_text.setVisibility(View.INVISIBLE);
                                holder.itemCardView.setCardBackgroundColor(Color.parseColor("#9ACD32"));
                                //Toast.makeText(fragment.getContext(),currPos,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return archiveDataList.size();
    }

    public static class ArchiveViewHolder extends RecyclerView.ViewHolder{

        private TextView goal_name_text, consistency_text,target_date_text,auto_del_text;
        private ProgressBar consistencyBar;
        private SwitchCompat autodelete;
        private CardView itemCardView;

        public ArchiveViewHolder(@NonNull View itemView) {
            super(itemView);

            goal_name_text= itemView.findViewById(R.id.archieve_goal_name);
            consistency_text= itemView.findViewById(R.id.archieve_goal_const);
            target_date_text= itemView.findViewById(R.id.archieve_target_date);
            consistencyBar = itemView.findViewById(R.id.consistency_progress);
            autodelete= itemView.findViewById(R.id.auto_delete);
            itemCardView= itemView.findViewById(R.id.item_cardView);
            auto_del_text= itemView.findViewById(R.id.auto_del_text);
        }
    }

}
