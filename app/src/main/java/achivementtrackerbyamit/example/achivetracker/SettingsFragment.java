package achivementtrackerbyamit.example.achivetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import achivementtrackerbyamit.example.achivetracker.auth.Users;
import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsFragment extends Fragment {




    private TextView rateus , share , privacypolicy;
    private Button showLogs, delAll;
    private DatabaseReference reference, tillActive;
    private String userID;
    CircleImageView profilePic;
    ImageView github;
    private ReviewInfo reviewInfo;
    private ReviewManager manager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilePic = view.findViewById(R.id.profile_pic);
        rateus = view.findViewById(R.id.rateus);
        share = view.findViewById(R.id.share);
        privacypolicy = view.findViewById(R.id.privacy);
        showLogs = view.findViewById(R.id.show_logs);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        github = view.findViewById(R.id.github_button);
        delAll = view.findViewById(R.id.delall);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        tillActive = reference.child(userID).child("Goals").child("Active");


        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/Tracky-Track-your-goals-or-targets"));
                startActivity(browserIntent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue().toString();
                        String send = "Hi. I'm " + name + " and I would like to invite you to install this app called Tracky \n" + "https://play.google.com/store/apps/details?id=achivementtrackerbyamit.example.achivetracker";
                        Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.banner);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/jpeg");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        share.putExtra(Intent.EXTRA_TEXT,send);
                        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), b, "Invite", null);
                        Uri imageUri =  Uri.parse(path);
                        share.putExtra(Intent.EXTRA_STREAM, imageUri);
                        startActivity(Intent.createChooser(share, "Select"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://pages.flycricket.io/tracky-track-your/privacy.html");
                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
            }
        });
        delAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delAll();
            }
        });

        // Rate Us Feature
        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager = ReviewManagerFactory.create(getActivity());
                com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // We can get the ReviewInfo object
                        reviewInfo = task.getResult();
                    } else {
                        // There was some problem, log or handle the error code.
                        Toast.makeText(getActivity(), "Review failed to start", Toast.LENGTH_SHORT).show();
                    }
                });
                if (reviewInfo!=null){
                    com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(getActivity(),reviewInfo);
                    flow.addOnCompleteListener(task -> {
                        Toast.makeText(getActivity(), "Rating is completed", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
        // Show logs button onClickListener
        showLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the list of activities
                reference.child(mAuth.getUid()).child("Activity").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List<DataSnapshot> logs = Lists.newArrayList(task.getResult().getChildren());

                            // Adding the list of activities to an array adapter
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.show_log_dialog);
                            for(int i=logs.size()-1;i>=0;i--)
                            {
                                adapter.add(logs.get(i).getValue(String.class));
                            }

                            // Creating alert dialog for showing logs
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                    .setTitle("Logs")
                                    .setAdapter(adapter,null)
                                    .setNegativeButton("Close",null)
                                    .create();
                            if(logs.size()==0) alertDialog.setMessage("No recent activity");
                            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.material_dialog_box);
                            alertDialog.show();
                        }
                    }
                });

            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    // Delete all Goal Feature
    private void delAll(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(),R.style.AlertDialogTheme);
        builder.setTitle("Delete all Goals");
        builder.setMessage("Are you sure you want to delete all goals");
        builder.setBackground(getResources().getDrawable(R.drawable.material_dialog_box,null));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                tillActive.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()) {
                            tillActive.removeValue();
                            Toast.makeText(getContext(), "All Goals removed successfully", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), HomeActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getContext(), "No Goals found to remove", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }

}