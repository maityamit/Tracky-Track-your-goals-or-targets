package achivementtrackerbyamit.example.achivetracker.rank;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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
import de.hdodenhof.circleimageview.CircleImageView;

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
        String goalName = snapshot.child("goal_Name").getValue(String.class);
        String consistencyNode = snapshot.child("consistency").getValue(String.class);
        int consistency = 0;
        try{
            consistency = Integer.parseInt(consistencyNode);
        }catch (NumberFormatException e){
            consistency = Integer.parseInt(goalName);
            goalName = consistencyNode;
        }
        holder.goalName.setText(goalName);
        if(consistency>=0) holder.goalConsistency.setText(consistency+"%");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        String key = snapshot.getKey();

        String finalGoalName = goalName;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getRootView().getContext());
                View dialogView = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.user_display, null);
                de.hdodenhof.circleimageview.CircleImageView cim = dialogView.findViewById(R.id.dialog_profile);
                TextView username = dialogView.findViewById(R.id.dialog_name);
                TextView desc = dialogView.findViewById(R.id.dialog_details);

                reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("name")) {
                            String n = snapshot.child("name").getValue().toString();
                            if(FirebaseAuth.getInstance().getUid().equals(key)) n="Me";
                            username.setText(n);
                            if(snapshot.hasChild("user_image")) {
                                Picasso.get().load(snapshot.child("user_image").getValue().toString()).into(cim);
                            }

                            desc.setText(finalGoalName);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.show();
            }
        });

        reference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshott) {

                if (snapshott.hasChild("name")){
                    String name = snapshott.child("name").getValue().toString();
                    if(FirebaseAuth.getInstance().getUid().equals(key)) name="Me";
                    holder.userName.setText(name);
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
