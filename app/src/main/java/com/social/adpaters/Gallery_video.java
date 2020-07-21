package com.social.adpaters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.social.Activity.Final_step;
import com.social.Activity.Interset;
import com.social.Activity.Post;
import com.social.Helpers.FileUtils;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ItemgellaryBinding;
import com.social.databinding.ItemintersetBinding;
import com.social.model.Model_Video;
import com.social.model.User_selcetion;
import com.social.videofilter.ui.activity.TrimVideoActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Gallery_video extends RecyclerView.Adapter<Gallery_video.Myview> {
    LayoutInflater layoutInflater;
    Context context;
    List<String> postModals;
    Post post;
    public Gallery_video(Context context, Post post, List<String> postModals) {
        this.context = context;
        this.postModals = postModals;
        this.post=post;
    }

    @NonNull
    @Override
    public Myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater ==null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemgellaryBinding postitemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.itemgellary,parent,false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        Utlity.Set_image_bitmap(context,postModals.get(position),holder.postitemBinding.videothumb);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path="";
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                   path=postModals.get(position);
                } else {
                    path = FileUtils.getPath(context, Uri.parse(postModals.get(position)));
                }
                context.startActivity(new Intent(context, TrimVideoActivity.class).putExtra("videoPath",path).putExtra("videothumb",path));
            }
        });
    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }

    public class Myview extends RecyclerView.ViewHolder {
       private final ItemgellaryBinding postitemBinding;
        public Myview(ItemgellaryBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
