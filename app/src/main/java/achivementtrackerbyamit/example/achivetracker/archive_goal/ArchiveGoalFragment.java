package achivementtrackerbyamit.example.achivetracker.archive_goal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leo.simplearcloader.SimpleArcLoader;


import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import achivementtrackerbyamit.example.achivetracker.R;

public class ArchiveGoalFragment extends Fragment {

    RecyclerView recyclerView;
    String currentUserID;
    DatabaseReference archiveDataRef;
    ArchiveAdapter archiveAdapter;
    ArrayList<DataSnapshot> dataList;
    ExtendedFloatingActionButton floatingActionButton;
    SimpleArcLoader mDialog;
    ImageView emptyArchive;
    static String dataKey="";
    String userID;
    static boolean flag=false;
    DatabaseReference reference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_archive_goal, container, false);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users");



        mDialog = view.findViewById(R.id.loader_archive_goal);
        floatingActionButton = view.findViewById(R.id.download_button);

        // ImageView displaying the empty archive message
        emptyArchive = (ImageView) view.findViewById(R.id.empty_archive_img);


        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();
        archiveDataRef= FirebaseDatabase.getInstance ().getReference ().child("Users").child(currentUserID).child("Archive_Goals");


        recyclerView = view.findViewById(R.id.archieve_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                archiveDataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()){
                            Toast.makeText(getActivity(), "No Archived Goals", Toast.LENGTH_SHORT).show();
                        }
                        //fetch all the data
                        if(snapshot.exists()){
                            View gh = view.findViewById(R.id.archieve_recycler_view);
                            share(screenShot(gh));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        return view;
    }


    private Bitmap screenShot(View view) {
        View screenView = view;
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void share(Bitmap bitmap){



        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String pathofBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap , "IMG_" + Calendar.getInstance().getTime(), null);


        if (!TextUtils.isEmpty(pathofBmp)){
            Uri uri = Uri.parse(pathofBmp);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tracky : track your Goal");
            //Retrieve value of completed goal using shared preferences from RetreiveData() function
            String goal_cmpltd = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("goal_completed","");
            //Retreive value of consistency using shared preferences from RetreiveData() function
            String goal_consistency = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("consistency","");
            //Retreive goal name using shared preferences from RetreiveData() function
            String goal_name = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("goal_name","");
            //Retreive name using Shared preference from Retrieve data function
            String user_name = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("name","");

            // Here You need to add code for issue
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "hello hello"));
        }


    }


    @Override
    public void onStart() {
        super.onStart();

        mDialog.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        dataList= new ArrayList<>();
        archiveAdapter= new ArchiveAdapter(getContext(),dataList);
        recyclerView.setAdapter(archiveAdapter);

        archiveDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()){
                    mDialog.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                //fetch all the data
                if(snapshot.exists()){
                    dataList = new ArrayList<>();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ArchiveClass archiveElement = dataSnapshot.getValue(ArchiveClass.class);

                        // Delete the archived goal if auto delete is on and seven days have passed
                        if(dataSnapshot.hasChild("deleteDate") && archiveElement.isAutoDelete() && timeExceed(archiveElement.getDeleteDate()))
                        {
                            dataSnapshot.getRef().removeValue();
                        }
                        // Otherwise add it to the list
                        else
                        {
                            dataList.add(dataSnapshot);
                        }
                    }
                    archiveAdapter.setArchiveDataList(dataList);
                    mDialog.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }

                // Making the empty archive message visible when the archive list is empty
                if(dataList.size()==0) emptyArchive.setVisibility(View.VISIBLE);
                else emptyArchive.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    public boolean timeExceed(String firstWord) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        String secondWord = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR)+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
        Date date1 = null, date2 = null;

        try {
            date1 = dateFormat.parse(firstWord);
            date2 = dateFormat.parse(secondWord);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = date2.getTime() - date1.getTime();
        long diff_in_days = (diff / (1000 * 60 * 60 * 24)) % 365;

        return diff_in_days >= 7;
    }



}