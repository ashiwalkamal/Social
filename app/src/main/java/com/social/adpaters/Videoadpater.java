package com.social.adpaters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.viewpager.widget.PagerAdapter;

import com.danikula.videocache.HttpProxyCacheServer;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.frgaments.Home;
import com.social.model.Video;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Videoadpater extends PagerAdapter {

    final Handler handler = new Handler();
    AudioManager audioManager;
    private Context mContext;
    private ArrayList<Video> contents;
    private HttpProxyCacheServer proxy;
    private Home home;
    private boolean is_mute = false;

    public Videoadpater(Context context, ArrayList<Video> contents, HttpProxyCacheServer proxy, Home home) {
        this.mContext = context;
        this.contents = contents;
        this.proxy = proxy;
        this.home = home;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup collection, int position) {
        Video video = contents.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.postsingle, collection, false);
        VideoView player = layout.findViewById(R.id.video);
        ProgressBar progressBar = layout.findViewById(R.id.progress_bar);
        ImageView thumb = layout.findViewById(R.id.thumb);
        ImageView userphoto = layout.findViewById(R.id.userphoto);
        LinearLayout fllow = layout.findViewById(R.id.llfollow);
        ImageView mute = layout.findViewById(R.id.mute);
        ImageView share = layout.findViewById(R.id.share);
        ImageView flag = layout.findViewById(R.id.flag);
        ImageView comment = layout.findViewById(R.id.comment);
        ImageView like = layout.findViewById(R.id.like);
        ImageView play = layout.findViewById(R.id.play);
        TextView username = layout.findViewById(R.id.username);
        TextView title = layout.findViewById(R.id.title);
        TextView hasgtags = layout.findViewById(R.id.hasgtags);
        Utlity.Set_image(mContext, video.getPhoto(), userphoto);

        if (video.isIslike()) {
            like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart_normal));
        } else {
            like.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_heart_1));
        }

        if (!TextUtils.isEmpty(video.getName()))
            username.setText(video.getName());
        else
            username.setText("User " + Utlity.getRandomNumber(1, 1000000));



        title.setText(video.getTitle());
        if(video.getTags()!=null&&video.getTags().size()!=0) {
            hasgtags.setVisibility(View.VISIBLE);

            for (String string : video.getTags()) {
                hasgtags.append(string+" ");
            }
        }
        else
        {
            hasgtags.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(video.getThumburl()))
        Utlity.Set_image(mContext,proxy.getProxyUrl(video.getThumburl()),thumb);

        player.setVideoPath(proxy.getProxyUrl(video.getVideourl()));
        thumb.setVisibility(View.VISIBLE);

        if (video.isPlay()) {
            progressBar.setVisibility(View.VISIBLE);
            player.start();
            mute.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_baseline_volume_off_24));
        } else {
            thumb.setVisibility(View.VISIBLE);
            player.pause();
        }

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    play.setVisibility(View.VISIBLE);
                    player.pause();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            play.setVisibility(View.GONE);
                        }
                    }, 2000);
                } else {
                    thumb.setVisibility(View.GONE);
                    player.start();
                    play.setVisibility(View.GONE);
                }

            }
        });
        fllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.follow(video.getUserid());
            }
        });

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("NewApi")
            @Override
            public void onPrepared(MediaPlayer mp) {
                thumb.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                mp.setLooping(true);
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            progressBar.setVisibility(View.GONE);
                            return true;
                        }
                        if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
                            progressBar.setVisibility(View.VISIBLE);
                            return true;
                        }
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            progressBar.setVisibility(View.VISIBLE);
                            return true;
                        }
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            progressBar.setVisibility(View.GONE);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.comment_box();
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!video.isIslike())
                    home.like(video);
            }
        });
        if (video.isIsfollow()) {
            fllow.setVisibility(View.GONE);
        } else {
            fllow.setVisibility(View.VISIBLE);
        }
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.share(video);
            }
        });
        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.flag(video);
            }
        });
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                if (is_mute) {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    is_mute = false;
                    mute.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_baseline_volume_off_24));
                } else {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    mute.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_baseline_volume_up_24));
                    is_mute = true;
                }
            }
        });
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}