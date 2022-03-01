package achivementtrackerbyamit.example.achivetracker.rank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import achivementtrackerbyamit.example.achivetracker.ProfileActivity;
import achivementtrackerbyamit.example.achivetracker.R;
import achivementtrackerbyamit.example.achivetracker.auth.Users;

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
        //holder.userName.setText(snapshot.child("name").getValue(String.class));
        holder.goalName.setText(snapshot.child("consistency").getValue(String.class));
        int consistency = Integer.parseInt(snapshot.child("goal_Name").getValue(String.class));
        if(consistency>=0) holder.goalConsistency.setText(consistency+"%");
       // Picasso.get().load(snapshot.child("user_image").getValue(String.class)).into(holder.goalImage);



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        String key = snapshot.getKey();

        reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshott) {

                if (snapshott.hasChild("name")){
                    holder.userName.setText(snapshott.child("name").getValue().toString());
                }

                if (snapshott.hasChild("user_image")){
                    Picasso.get().load(snapshott.child("user_image").getValue().toString()).error(R.drawable.profile).placeholder(R.drawable.profile).into(holder.goalImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(), "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });



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
