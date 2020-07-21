package com.social.Activity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.danikula.videocache.HttpProxyCacheServer;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ActivityViddeoPlayBinding;
import com.social.model.Video;

public class Video_play extends AppCompatActivity {
    Video video;
    ActivityViddeoPlayBinding binding;
    HttpProxyCacheServer proxy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_viddeo_play);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        proxy = new HttpProxyCacheServer(getApplicationContext());
        if (getIntent() != null) {
            video = Utlity.gson.fromJson(getIntent().getStringExtra("post"), Video.class);
            Utlity.Set_image(this,proxy.getProxyUrl(video.getThumburl()),binding.thumb);
            binding.thumb.setVisibility(View.VISIBLE);
            binding.title.setText(video.getTitle());
            binding.username.setText(video.getName());
            Utlity.Set_image(this, video.getPhoto(), binding.userphoto);
            binding.video.setVideoPath(proxy.getProxyUrl(video.getVideourl()));
            binding.video.start();
            binding.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @SuppressLint("NewApi")
                @Override
                public void onPrepared(MediaPlayer mp) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.thumb.setVisibility(View.GONE);
                    mp.setLooping(true);
                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                binding.progressBar.setVisibility(View.GONE);
                                return true;
                            }
                            if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
                                binding.progressBar.setVisibility(View.VISIBLE);
                                return true;
                            }
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                                binding.progressBar.setVisibility(View.VISIBLE);
                                return true;
                            }
                            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                                binding.progressBar.setVisibility(View.GONE);
                                return true;
                            }
                            return false;
                        }
                    });
                }
            });
        }
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}