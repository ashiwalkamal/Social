package com.social.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.DB.DBHelper;
import com.social.Helpers.FileUtils;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.adpaters.Hash_Adpater;
import com.social.databinding.ActivityFinalStepBinding;
import com.social.frgaments.Home;
import com.social.model.Video;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class Final_step extends AppCompatActivity {
    ActivityFinalStepBinding binding;
    String video_path = "";
    LatLng latLng;
    ArrayList<String> tags = new ArrayList<>();
    Hash_Adpater adpater;
    StorageReference storageReference;
    FirebaseStorage storage;
    Video video;
    FirebaseFirestore db;
    Bitmap bitmap = null;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference counter;
    private DBHelper mydb;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_final_step);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Locationpermission();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        counter = database.getReference("counter");
        counter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                count = snapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (getIntent() != null) {
            video_path = getIntent().getStringExtra("path");

            try {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ThumbnailUtils.createVideoThumbnail(video_path, MediaStore.Video.Thumbnails.MINI_KIND);
                } else {
                    bitmap = ThumbnailUtils.createVideoThumbnail(FileUtils.getPath(Final_step.this, Uri.parse(video_path)), MediaStore.Video.Thumbnails.MINI_KIND);
                }
                binding.thumb.setImageBitmap(bitmap);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                binding.thumb.setImageDrawable(this.getResources().getDrawable(R.drawable.empty));

            }

            binding.video.setVideoPath(video_path);

        }
        Utlity.Set_image(this, String.valueOf(MainActivity.firebaseUser.getPhotoUrl()), binding.userphoto);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.thumb.setVisibility(View.GONE);
                binding.video.setVisibility(View.VISIBLE);
                binding.play.setVisibility(View.GONE);
                binding.video.start();
            }
        });
        binding.video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.thumb.setVisibility(View.VISIBLE);
                binding.video.setVisibility(View.GONE);
                binding.play.setVisibility(View.VISIBLE);
            }
        });

        binding.publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.desc.getText().toString())) {
                    if (Utlity.is_online(Final_step.this)) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            try {
                                uploadFile(video_path);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                uploadFile(FileUtils.getPath(Final_step.this, Uri.parse(video_path)));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Utlity.snakbar(binding.desc, getString(R.string.invaliddesc));
                }

            }
        });


    }

    public boolean Locationpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                latLng = Utlity.getcurrtlocation(this, binding.location);
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            //to set galleryvideo
            latLng = Utlity.getcurrtlocation(this, binding.location);
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            latLng = Utlity.getcurrtlocation(this, binding.location);
        }
        else
        {
            Utlity.show_toast(Final_step.this,"Location Permission Required !" );
        }
    }
    //this method will upload the file
    private void uploadFile(String filePath) throws FileNotFoundException {
        //if there is a file to upload
        if (filePath != null) {
            String file_name = System.currentTimeMillis() + ".mp4";
            String thumb_name = System.currentTimeMillis() + ".jpg";
            //displaying a progress dialog while upload is going on
            Utlity.show_progress(this);
            final StorageReference riversRef = storageReference.child("uservideo/" + file_name);
            final StorageReference thumb = storageReference.child("videothumb/" + thumb_name);

            InputStream stream = new FileInputStream(new File(filePath));

            riversRef.putStream(stream)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riversRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String video_thumb = task.getResult().toString();
                                    String thumb_path = getImageUri(Final_step.this, bitmap);
                                    File userimgs = new File(thumb_path);
                                    File now_file = null;
                                    try {
                                        now_file = new Compressor(Final_step.this).compressToFile(userimgs);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    InputStream stream = null;
                                    try {
                                        stream = new FileInputStream(now_file);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    thumb.putStream(stream)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    thumb.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {

                                                            int id = count + 1;
                                                            video = new Video();
                                                            video.setId(id);
                                                            if (latLng == null) {
                                                                latLng = new LatLng(0.0, 0.0);
                                                            }
                                                            video.setLat(latLng.latitude);
                                                            video.setLng(latLng.longitude);
                                                            video.setLocation(binding.location.getText().toString());
                                                            video.setTitle(binding.desc.getText().toString());
                                                            video.setTags(binding.hasgtags.getValues());
                                                            video.setUserid(MainActivity.firebaseUser.getUid());
                                                            video.setName(MainActivity.firebaseUser.getDisplayName());
                                                            video.setPhoto(String.valueOf(MainActivity.firebaseUser.getPhotoUrl()));
                                                            video.setVideourl(video_thumb);
                                                            video.setThumburl(task.getResult().toString());
                                                            if (db != null) {
                                                                /// mDatabase.child("user_post").child(firebaseUser.getUid()+"_"+System.currentTimeMillis()).setValue(video);
                                                                db.collection("post").document(String.valueOf(System.currentTimeMillis()))
                                                                        .set(video)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Utlity.show_toast(Final_step.this, "Your Post has been publish !");
                                                                                mDatabase.child("counter").setValue(id);
                                                                                Home.posts.add(video);
                                                                                Utlity.dismiss_dilog(Final_step.this);
                                                                                MainActivity.allready = false;
                                                                                MainActivity.binding.navview.setSelectedItemId(R.id.navigation_home);
                                                                                finish();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Utlity.dismiss_dilog(Final_step.this);
                                                                                Utlity.show_alert(Final_step.this, e.getMessage(), false);

                                                                                Log.w("Error", "Error writing document", e);
                                                                            }
                                                                        });

                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    //if the upload is not successfull
                                                    //hiding the progress dialog
                                                    Utlity.dismiss_dilog(Final_step.this);
                                                    //and displaying error message
                                                    Utlity.show_alert(Final_step.this, exception.getMessage(), false);
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            Utlity.dismiss_dilog(Final_step.this);
                            //and displaying error message
                            Utlity.show_alert(Final_step.this, exception.getMessage(), false);
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
            Utlity.show_alert(Final_step.this, "File Path missing", false);
        }
    }

    public String getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return getRealPathFromURI(Uri.parse(path));
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}