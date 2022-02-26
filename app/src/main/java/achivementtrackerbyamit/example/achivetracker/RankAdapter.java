package achivementtrackerbyamit.example.achivetracker;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class RankViewHolder extends RecyclerView.ViewHolder{
        TextView goalRank,goalConsistency,goalName,userName;
        ImageView goalImage;
        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            goalRank = itemView.findViewById(R.id.goal_rank);
            goalConsistency = itemView.findViewById(R.id.goal_consistency);
            goalName = itemView.findViewById(R.id.goal_name);
            userName = itemView.findViewById(R.id.user_name);
            goalImage = itemView.findViewById(R.id.goal_user_pfp);
        }
    }
}
