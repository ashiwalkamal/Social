package com.social.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.social.Helpers.Utlity;
import com.social.R;
import com.social.adpaters.Gallery_video;
import com.social.databinding.ActivityPostBinding;
import com.social.videofilter.ui.activity.TrimVideoActivity;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.social.Activity.MainActivity.gallery;

public class Post extends Fragment {
    private static final String TAG = "persmission";
    private static final int VIDEO_CAPTURE = 101;
    ActivityPostBinding binding;
    Gallery_video gallery_video;
    GridLayoutManager layoutManager;
    File mediaFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_post, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + System.currentTimeMillis() + ".mp4");
        binding.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(runtime()) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    Uri videoUri = Uri.fromFile(mediaFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    startActivityForResult(intent, VIDEO_CAPTURE);
                }

            }
        });
        layoutManager = new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        binding.galleryvideo.setLayoutManager(layoutManager);
        gallery_video = new Gallery_video(getActivity(), Post.this, gallery);
        binding.galleryvideo.setAdapter(gallery_video);
        return binding.getRoot();
    }


    public boolean runtime() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0);
            return false;
        } else {

            return true;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(getActivity(), TrimVideoActivity.class).putExtra("videoPath", mediaFile.getAbsolutePath()).putExtra("videothumb", mediaFile.getAbsolutePath()));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    Uri videoUri = Uri.fromFile(mediaFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    startActivityForResult(intent, VIDEO_CAPTURE);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Utlity.show_toast(getActivity(),"Permission denied to record video");
                }
                return;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}