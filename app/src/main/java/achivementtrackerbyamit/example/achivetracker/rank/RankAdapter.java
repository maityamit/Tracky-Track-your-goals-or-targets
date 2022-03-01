package achivementtrackerbyamit.example.achivetracker.rank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import achivementtrackerbyamit.example.achivetracker.R;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {

    Context context;
    List<DataSnapshot> rankList;

    public RankAdapter(Context context, List<DataSnapshot> rankList) {
        this.context = context;
        this.rankList = rankList;
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_list_item,parent,false);
        return new RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        DataSnapshot snapshot = rankList.get(position);
        holder.goalRank.setText((position+4)+".");
        holder.userName.setText(snapshot.child("name").getValue(String.class));
        holder.goalName.setText(snapshot.child("goal_Name").getValue(String.class));
        holder.goalConsistency.setText(snapshot.child("consistency").getValue(String.class)+"%");
        Picasso.get().load(snapshot.child("user_image").getValue(String.class)).into(holder.goalImage);
    }

    @Override
    public int getItemCount() {
        return rankList.size();
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
