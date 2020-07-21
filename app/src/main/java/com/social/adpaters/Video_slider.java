package com.social.adpaters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.social.Activity.Final_step;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ItemhashBinding;
import com.social.databinding.PostsingleBinding;
import com.social.model.Video;

import java.util.ArrayList;

public class Video_slider extends RecyclerView.Adapter<Video_slider.Myview> {
    LayoutInflater layoutInflater;
    Context context;
    ArrayList<Video> postModals;
    private HttpProxyCacheServer proxy;
    final Handler handler = new Handler();
    public Video_slider(Context context,  ArrayList<Video> postModals, HttpProxyCacheServer proxy) {
        this.context = context;
        this.postModals = postModals;
        this.proxy = proxy;
    }

    @NonNull
    @Override
    public Myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater ==null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        PostsingleBinding postitemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.postsingle,parent,false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        Video video=postModals.get(position);
        holder.postitemBinding.title.setText(video.getTitle());
       // holder.postitemBinding.video.setVideoPath(proxy.getProxyUrl(video.getVideourl()));
        if (video.isPlay()) {
           holder.postitemBinding.progressBar.setVisibility(View.VISIBLE);
          /// holder.postitemBinding.video.start();
        } else {
           // holder.postitemBinding.video.pause();
        }
//        holder.postitemBinding.video.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.postitemBinding.play.setVisibility(View.VISIBLE);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Do something after 100ms
//                        holder.postitemBinding.play.setVisibility(View.GONE);
//                    }
//                }, 2000);
//            }
//        });
//        holder.postitemBinding.play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(holder.postitemBinding.video.isPlaying())
//                {
//                    holder.postitemBinding.play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
//
//                    holder.postitemBinding.video.pause();
//                }
//                else
//                {
//                    holder.postitemBinding.play.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_24));
//                    holder.postitemBinding.video.start();
//                }
//
//            }
//        });


//        holder.postitemBinding.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @SuppressLint("NewApi")
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                holder.postitemBinding.progressBar.setVisibility(View.GONE);
//                mp.setLooping(true);
//                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//                    @Override
//                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
//                            holder.postitemBinding.progressBar.setVisibility(View.GONE);
//                            return true;
//                        }
//                        if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
//                            holder.postitemBinding.progressBar.setVisibility(View.VISIBLE);
//                            return true;
//                        }
//                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
//                            holder.postitemBinding.progressBar.setVisibility(View.VISIBLE);
//                            return true;
//                        }
//                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
//                            holder.postitemBinding.progressBar.setVisibility(View.GONE);
//                            return true;
//                        }
//                        return false;
//                    }
//                });
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }

    public class Myview extends RecyclerView.ViewHolder {
       private final PostsingleBinding postitemBinding;
        public Myview(PostsingleBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
