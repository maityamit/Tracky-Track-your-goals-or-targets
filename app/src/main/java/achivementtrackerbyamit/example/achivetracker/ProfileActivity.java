package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    TextView welcome1,welcome2;
    private DatabaseReference reference;
    ProgressDialog progressDialog;
    private String userID;
    AppCompatButton Logout;
    ImageButton profilePicButton;
    CircleImageView profilePic;
    private final int GALLERY_INTENT_CODE = 993;
    private final int CAMERA_INTENT_CODE = 990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        showProgressDialog();

        welcome1 = findViewById(R.id.user_name);
        welcome2 = findViewById(R.id.users_email);
        Logout = findViewById(R.id.logout);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid ();

        reference = FirebaseDatabase.getInstance().getReference("Users");

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Are you sure want to Logout ? ")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                FirebaseAuth.getInstance().signOut();
                                Intent loginIntenttt = new Intent ( ProfileActivity.this,SplasshActivity.class );
                                loginIntenttt.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                startActivity ( loginIntenttt );
                                finish ();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class);

                if (userprofile != null){
                    String fullname = userprofile.name;
                    String email = userprofile.email;

                    welcome1.setText(fullname);
                    welcome2.setText(email);

                }

                // Getting the url of profile picture
                Object pfpUrl = snapshot.child("user_image").getValue();
                if(pfpUrl != null)
                {
                    // If the url is not null, then adding the image
                    Picasso.get().load(pfpUrl.toString()).into(profilePic);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // Button for adding profile pic
        profilePicButton = (ImageButton) findViewById(R.id.profile_pic_button);
        profilePic = (CircleImageView) findViewById(R.id.profile_pic);

        // OnClickListener for Profile Pic Button
        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Showing options for getting image to set as profile picture
                new AlertDialog.Builder(ProfileActivity.this).setTitle("Change profile photo").setItems(new String[]{"Choose from gallery", "Take a new picture"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i)
                        {
                            // Choosing image from gallery
                            case 0:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryIntent.setType("image/*");
                                if(galleryIntent.resolveActivity(getPackageManager())!=null)
                                {
                                    startActivityForResult(galleryIntent,GALLERY_INTENT_CODE);
                                }
                                break;

                            // Clicking a new picture using camera
                            case 1:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if(cameraIntent.resolveActivity(getPackageManager())!=null)
                                {
                                    startActivityForResult(cameraIntent,CAMERA_INTENT_CODE);
                                }
                                break;
                        }
                    }
                }).show();
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_diaglog);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//        Runnable progressRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (confirmation != 1) {
//                    progressDialog.cancel();
//                    Toast.makeText(DashboardActivity.this, "Fetching data from Firebase", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//
//        Handler pdCanceller = new Handler();
//        pdCanceller.postDelayed(progressRunnable, 5000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null)
        {
            Uri uri = (Uri) data.getData();
            switch (requestCode)
            {
                // Image received from gallery
                case GALLERY_INTENT_CODE:
                    try {
                        // Converting the image uri to bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        profilePic.setImageBitmap(bitmap);
                        updateProfilePic(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                // Image received from camera
                case CAMERA_INTENT_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profilePic.setImageBitmap(bitmap);
                    updateProfilePic(bitmap);
                    break;
            }
        }
    }

    // Function for updating profile picture
    private void updateProfilePic(Bitmap bitmap)
    {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())+"/pfp.jpg");
        showProgressDialog();

        // Converting image bitmap to byte array for uploading to firebase storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] pfp = baos.toByteArray();

        // Uploading the byte array to firebase storage
        storageReference.putBytes(pfp).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    // Getting url of the image uploaded to firebase storage
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                            {
                                // Setting the image url as the user_image property of the user in the database
                                String pfpUrl = task.getResult().toString();
                                reference.child(userID).child("user_image").setValue(pfpUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(ProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}