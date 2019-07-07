package com.faizal.shadab.lapitchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import id.zelory.compressor.Compressor;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    private static final String USERS = "users";

    //UI related
    private ImageView imgProfile;
    private TextView txtName;
    private TextView txtStatus;
    private TextView txtAccountSettings;
    private TextView txtLogOut;
    private ProgressBar progressBar;

    Uri imageUrifromGallery = null;
    Uri croppedImageUri = null;
    private Uri uploadedImageUri;

    //Firebase related
    String  mUserUid;
    DatabaseReference mCurrentUserReference;
    private StorageReference firebaseStorageReference;

    //CONSTANT FIELDS
    private static final String[] PERMISSION_READ_WRITE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int READ_WRITE_PERMISSION_CODE = 10;
    private static final int PHOTO_PICKER_INTENT_CODE = 15;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //UI components
        imgProfile = findViewById(R.id.settings_img_profile);
        txtName = findViewById(R.id.settings_txt_name);
        txtStatus = findViewById(R.id.settings_txt_status);
        txtAccountSettings = findViewById(R.id.settings_txt_ac_settings);
        txtLogOut = findViewById(R.id.settings_txt_log_out);
        progressBar = findViewById(R.id.progressBar);

        txtAccountSettings.setOnClickListener(this);
        txtLogOut.setOnClickListener(this);
        imgProfile.setOnClickListener(this);


        //Firebase
        mUserUid = FirebaseAuth.getInstance().getUid();
        mCurrentUserReference = FirebaseDatabase.getInstance().getReference().child(USERS).child(mUserUid);
        firebaseStorageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUserUid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String display_picture = dataSnapshot.child("display_picture").getValue().toString();
                if(!display_picture.equals("default")){
                    Glide.with(Settings.this)
                            .load(display_picture)
                            .centerCrop()
                            .placeholder(R.drawable.ic_power_settings_white)
                            .into(imgProfile);
                }
                txtName.setText(name);
                txtStatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mCurrentUserReference.addValueEventListener(eventListener);
    }

    public void sendToAccountSettings(){
        Intent accountSettingsIntent = new Intent(Settings.this, AccountSettings.class);
        startActivity(accountSettingsIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settings_txt_ac_settings:
                sendToAccountSettings();
                break;

            case R.id.settings_txt_log_out:
                logOut();
                break;

            case R.id.settings_img_profile:
                //ask for permission if
                // --android version is above marshmallow &&
                // --permission is not granted
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ContextCompat.checkSelfPermission(Settings.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(Settings.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Settings.this, PERMISSION_READ_WRITE, READ_WRITE_PERMISSION_CODE);
                }else {
                    // Permissions already Granted
                    chosePhoto();
                    // result will be published in onActivityResult
                }
                break;
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent homeIntent = new Intent(Settings.this, HomeActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
    private void chosePhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_PICKER_INTENT_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_WRITE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(Settings.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            chosePhoto();
        } else {
            Toast.makeText(Settings.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICKER_INTENT_CODE && resultCode == RESULT_OK){
            imageUrifromGallery = data.getData();
            CropImage.activity(imageUrifromGallery).setAspectRatio(1,1)
                    .start(Settings.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                croppedImageUri = result.getUri();
                imgProfile.setImageURI(croppedImageUri);
                uploadImage();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(Settings.this,"Error:" + result.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Upload the profile image with the name of @user_uid
    //in storage/users/user_uid.jpg
    private void uploadImage(){
        progressBar.setVisibility(View.VISIBLE);

        File thumb_file = new File(croppedImageUri.getPath());

        try {
            Bitmap thumb_bitmap = new Compressor(this)
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(75)
                    .compressToBitmap(thumb_file);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] thumb_byte = baos.toByteArray();

            final StorageReference uploadThumb = firebaseStorageReference.child("profile").child("thumb").child(mUserUid + "jpg");
            uploadThumb.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Settings.this, "Thumb Uploaded", Toast.LENGTH_SHORT).show();
                        uploadThumb.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child("thumb_picture").setValue(uri.toString());
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Settings.this, "Error uploading thumb: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        final StorageReference uploadedImagePath = firebaseStorageReference.child("profile").child(mUserUid + ".jpg");
        uploadedImagePath.putFile(croppedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Settings.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();



                    //Getting download url to retrieve image later
                    uploadedImagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadedImageUri = uri;

                            //Saving profile image url to the database
                            databaseReference.child("display_picture").setValue(uploadedImageUri.toString());


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Settings.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(Settings.this, "Err: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.i("Error", task.getException().getMessage());
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
