package com.social.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.adpaters.Profile_tabadpater;
import com.social.databinding.ActivityMyaccountBinding;
import com.social.frgaments.Profile;
import com.social.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class Myaccount extends AppCompatActivity implements View.OnClickListener {
    ActivityMyaccountBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    FirebaseStorage storage;
    String photo = "";
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_myaccount);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        mAuth = instance;
        firebaseUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ref = database.getReference("users").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user=snapshot.getValue(User.class);
                //set profile
                if (firebaseUser != null) {
                    setprofile(firebaseUser);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //click
        click();
    }

    private void click() {
        binding.close.setOnClickListener(this);
        binding.userphoto.setOnClickListener(this);
        binding.saveProfile.setOnClickListener(this);
    }

    private void setprofile(FirebaseUser firebaseUser) {
        if (!TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            binding.name.setText(firebaseUser.getDisplayName());

        } else {
            binding.name.setText("User" + Utlity.getRandomNumber(1, 1000000));
        }
        if(user!=null) {
            binding.email.setText(user.getEmail());
            binding.mobileno.setText(user.getMobile());
            binding.aboutme.setText(user.getAbout());
        }
        else
        {
            binding.email.setText(firebaseUser.getEmail());
            binding.mobileno.setText(firebaseUser.getPhoneNumber());
        }

        Utlity.Set_image(this, String.valueOf(firebaseUser.getPhotoUrl()), binding.userphoto);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if (!Utlity.is_empty(returnValue.get(0))) {
                Log.d("profile_photo", returnValue.get(0));
                getBitmap(returnValue.get(0),binding.userphoto);
                photo = returnValue.get(0);
            }
            else
            {
                Utlity.show_toast(this,"Unable to select Image !");
            }
        }
    }

    //this method will upload the file
    private void uploadFile(String filePath) throws FileNotFoundException {
        //if there is a file to upload
        if (filePath != null) {
            String file_name = System.currentTimeMillis() + ".jpg";
            //displaying a progress dialog while upload is going on
           Utlity.show_progress(this);
            final StorageReference riversRef = storageReference.child("userprofile/" + file_name);
            File now_file = null;
            try {
                now_file = new Compressor(this).compressToFile(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream stream = new FileInputStream(now_file);

            riversRef.putStream(stream)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            //this is the new way to do it
                            riversRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    photo = task.getResult().toString();
                                    Utlity.Set_image(Myaccount.this, photo, binding.userphoto);
                                    save_profile();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            Utlity.dismiss_dilog(Myaccount.this);
                            //and displaying error message
                            Utlity.show_toast(Myaccount.this, exception.toString());
                        }
                    });
        }
    }

    private void save_profile() {
        String name = binding.name.getText().toString();
        if (!Utlity.is_empty(name)) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(photo))
                    .build();
            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Utlity.dismiss_dilog(Myaccount.this);
                                User user=new User();
                                user.setUid(firebaseUser.getUid());
                                user.setEmail(binding.email.getText().toString());
                                user.setMobile(binding.mobileno.getText().toString());
                                user.setAbout(binding.aboutme.getText().toString());
                                mDatabase.child("users").child(firebaseUser.getUid()).setValue(user);
                                Utlity.show_toast(Myaccount.this, "Profile Update Successfully !");
                            }
                        }
                    });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                //close the screen
               finish();
                Profile.change_profile();
                break;
            case R.id.userphoto:
                Pix.start(this, Options.init().setRequestCode(100).setExcludeVideos(true));
                break;
            case R.id.save_profile:
                if(!TextUtils.isEmpty(binding.name.getText())) {
                    if(!TextUtils.isEmpty(photo)) {
                        if (Utlity.is_online(this)) {
                            try {
                                uploadFile(photo);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utlity.show_toast(this, "No Internet !");
                        }
                    }
                    else
                    {
                        Utlity.show_progress(this);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(binding.name.getText().toString())
                                .build();
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Utlity.dismiss_dilog(Myaccount.this);
                                        if (task.isSuccessful()) {
                                            User user=new User();
                                            user.setUid(firebaseUser.getUid());
                                            user.setEmail(binding.email.getText().toString());
                                            user.setMobile(binding.mobileno.getText().toString());
                                            user.setAbout(binding.aboutme.getText().toString());
                                            mDatabase.child("users").child(firebaseUser.getUid()).setValue(user);
                                            Utlity.show_toast(Myaccount.this, "Profile Update Successfully !");
                                        }
                                        else
                                        {
                                            Utlity.show_toast(Myaccount.this,task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                }
                else
                {
                    binding.name.setError(getString(R.string.emptyname));
                }
                break;
        }
    }

    public Bitmap getBitmap(String path, ImageView imageView) {
        Bitmap bitmap=null;
        try {
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap ;
    }
}