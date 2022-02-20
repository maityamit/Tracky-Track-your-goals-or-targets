package achivementtrackerbyamit.example.achivetracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    TextView welcome1, welcome2;
    private DatabaseReference reference, tillActive;
    ProgressDialog progressDialog;
    private String userID;
    AppCompatButton Logout;
    ImageButton profilePicButton;
    ImageView editname;
    CircleImageView profilePic;
    private final int GALLERY_INTENT_CODE = 993;
    private final int CAMERA_INTENT_CODE = 990;
    final private int REQUEST_CODE_PERMISSION = 111;
    ArrayList<String> GoalName;
    String fileName;
    File pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        InitializationMethod();
        getUserDatafromFirebase();

        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditName();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogOutMethod();

            }
        });
        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowOptionsforProfilePic();

            }
        });
    }

    private void InitializationMethod() {
        welcome1 = findViewById(R.id.users_name);
        welcome2 = findViewById(R.id.users_email);
        Logout = findViewById(R.id.logout);
        editname = findViewById(R.id.editName);

        // Button for adding profile pic
        profilePicButton = (ImageButton) findViewById(R.id.profile_pic_button);
        profilePic = (CircleImageView) findViewById(R.id.profile_pic);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users");

        tillActive = FirebaseDatabase.getInstance ().getReference ().child("Users").child(userID).child("Goals").child("Active");
    }

    private void getUserDatafromFirebase() {
        showProgressDialog();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userprofile = snapshot.getValue(Users.class);

                if (userprofile != null) {
                    String fullname = userprofile.name;
                    String email = userprofile.email;

                    welcome1.setText(fullname);
                    welcome2.setText(email);

                }

                // Getting the url of profile picture
                Object pfpUrl = snapshot.child("user_image").getValue();
                if (pfpUrl != null) {
                    // If the url is not null, then adding the image
                    Picasso.get().load(pfpUrl.toString()).placeholder(R.drawable.profile).error(R.drawable.profile).into(profilePic);
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Cannot fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Showing options for getting image to set as profile picture
    private void ShowOptionsforProfilePic() {

        new MaterialAlertDialogBuilder(ProfileActivity.this).setBackground(getResources().getDrawable(R.drawable.material_dialog_box)).setTitle("Change profile photo").setItems(new String[]{"Choose from gallery", "Take a new picture"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    // Choosing image from gallery
                    case 0:
                        // Defining Implicit Intent to mobile gallery
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(
                                Intent.createChooser(
                                        intent,
                                        "Select Image from here..."),
                                GALLERY_INTENT_CODE);
                        break;

                    // Clicking a new picture using camera
                    case 1:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, CAMERA_INTENT_CODE);
                        }
                        startActivityForResult(cameraIntent, CAMERA_INTENT_CODE);
                        break;
                }
            }
        }).show();

    }

    // Function for logout method
    private void LogOutMethod() {
        new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Are you sure want to Logout ? ")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        FirebaseAuth.getInstance().signOut();
                        Intent loginIntenttt = new Intent(ProfileActivity.this, SplasshActivity.class);
                        loginIntenttt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntenttt);
                        finish();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    // Function for progress dialoge bar
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
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = (Uri) data.getData();
            switch (requestCode) {
                // Image received from gallery
                case GALLERY_INTENT_CODE:
                    try {
                        // Converting the image uri to bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
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
    private void updateProfilePic(Bitmap bitmap) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()) + "/pfp.jpg");
        showProgressDialog();

        // Converting image bitmap to byte array for uploading to firebase storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] pfp = baos.toByteArray();

        // Uploading the byte array to firebase storage
        storageReference.putBytes(pfp).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // Getting url of the image uploaded to firebase storage
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // Setting the image url as the user_image property of the user in the database
                                String pfpUrl = task.getResult().toString();
                                reference.child(userID).child("user_image").setValue(pfpUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Function for github link view
    public void GithubLinkClick(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/maityamit/Tracky-Track-your-goals-or-targets"));
        startActivity(browserIntent);

    }

    public void EditName() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(ProfileActivity.this); //Created alert Dialog
        mydialog.setTitle("Enter your new name"); //Title of EditText
        final EditText weightInput = new EditText(ProfileActivity.this);
        weightInput.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        mydialog.setView(weightInput);
        mydialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String myText = weightInput.getText().toString(); //Saving Entered name in String

                reference.child(userID).child("name").setValue(myText); //calling child and setting
                welcome1.setText(myText);
            }
        });
        mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel(); //cancel button
            }
        });
        mydialog.show();
    }
    public void PDF(View view) {
        GoalName = new ArrayList<>(); //Initialize
        tillActive.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GoalName.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) { //get all Goal IDs
                    GoingCLass going = snapshot1.getValue(GoingCLass.class);
                    String s = going.getGoalName(); //Get data of Goal Name from that ID
                    GoalName.add(s); //add in arraylist
                    // Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Naming(); //Dialog EditText
    }

    private void Naming() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(ProfileActivity.this); //Created alert Dialog
        mydialog.setTitle("Enter PDF name"); //Title of EditText
        final EditText f = new EditText(ProfileActivity.this);
        f.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        mydialog.setView(f);
        mydialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName = f.getText().toString(); //Saving Entered name in String
                createPDF();
            }
        });
        mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel(); //cancel button
            }
        });
        mydialog.show();
    }

    private void createPDF() {
        int hasWritePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    new AlertDialog.Builder(this)
                            .setMessage("Access Storage Permission")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
            return;
        } else {
            File docPath = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if (!docPath.exists()) {
                docPath.mkdir();
            }
            pdf = new File(docPath.getAbsolutePath(), fileName + ".pdf");
            try {
                OutputStream stream = new FileOutputStream(pdf);

                Document document = new Document();
                PdfWriter.getInstance(document, stream);
                document.open();
                document.add(new Paragraph("Your Active Goals are: \n"));
                for(String s : GoalName) {
                    document.add(new Paragraph(s));
                }
                document.close();
                Snackbar snacbar = Snackbar.make(findViewById(android.R.id.content), fileName + " Saved: " + pdf.toString(), Snackbar.LENGTH_SHORT);
                snacbar.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}