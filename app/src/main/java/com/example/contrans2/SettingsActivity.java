package com.example.contrans2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");



        Initializefields();

        userName.setVisibility(View.INVISIBLE);


        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        RetrieveUserInfo();


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        
    }



    private void Initializefields() {
        UpdateAccountSettings=(Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();


                //Dit hele stuk tot "UpdateSettings void" heb ik van youtube tutorial gehaald 25
                final Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String DownloadUrl = uri.toString();
                                        RootRef.child("Users").child(currentUserID).child("image").setValue(DownloadUrl)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SettingsActivity.this, "Profile image stored to database successfully.", Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();
                                                        } else {
                                                            String message = task.getException().getMessage();
                                                            Toast.makeText(SettingsActivity.this, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();

                                                        }


                                                    }
                                                });


                                    }

                                });
                            }
                        });
            }}}




    private void UpdateSettings()
    {
     String setUserName = userName.getText().toString();
     String setStatus = userStatus.getText().toString();

     if(TextUtils.isEmpty(setUserName))
     {
         Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
     }
     if (TextUtils.isEmpty(setStatus)){
         Toast.makeText(this, "Please set your status...", Toast.LENGTH_SHORT).show();
     }
     else {
         //zodra username en status is ingevuld dan storen we deze in de firebase database
         HashMap<String, String> profileMap = new HashMap<>();
         profileMap.put("uid", currentUserID);
         profileMap.put("name", setUserName);
         profileMap.put("status", setStatus);
         //alles wat ingevuld is, gaan we storen in de users, currentuserid en profilemap
         RootRef.child("Users").child(currentUserID).setValue(profileMap)
                 //Als alles is gelukt
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             SendUserToMainActivity();
                             Toast.makeText(SettingsActivity.this, "Profile Update Succesfully", Toast.LENGTH_SHORT).show();
                         }
                         else
                         {
                             //vertelt waarom het niet gelukt is
                             String message = task.getException().toString();
                             //toast + error message
                             Toast.makeText(SettingsActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();

                         }

                     }
                 });
     }


    }


    //Retrieve current User info from database
    private void RetrieveUserInfo()
    {
    RootRef.child("Users").child(currentUserID)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //if exists these values name and image will be displayed
                    if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))) {
                        String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                        String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                        String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                        userName.setText(retrieveUserName);
                        userStatus.setText(retrieveStatus);
                        Picasso.get().load(retrieveProfileImage).into(userProfileImage) ;

                    } else if
                    ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                        String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                        String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                        userName.setText(retrieveUserName);
                        userStatus.setText(retrieveStatus);
                    } else {
                        userName.setVisibility(View.VISIBLE);
                        Toast.makeText(SettingsActivity.this, "Please set & update your profile", Toast.LENGTH_SHORT).show();

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                }
            });
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        //zorgt ervoor als users op de back knop drukken dus niet terug kunnen naar login, en dus op mainactivity blijven
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}


