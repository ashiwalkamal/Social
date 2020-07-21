package com.social.adpaters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.social.Activity.Post;
import com.social.Activity.Sound_selector;
import com.social.Helpers.FileUtils;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ItemgellaryBinding;
import com.social.databinding.SoundItemBinding;
import com.social.model.AudioModel;
import com.social.videofilter.ui.activity.TrimVideoActivity;

import java.util.ArrayList;
import java.util.List;

public class Sound_adpater extends RecyclerView.Adapter<Sound_adpater.Myview> {
    LayoutInflater layoutInflater;
    Context context;
    ArrayList<AudioModel> postModals;
    Sound_selector post;
    public Sound_adpater(Context context, Sound_selector post, ArrayList<AudioModel> postModals) {
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
        SoundItemBinding postitemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.sound_item,parent,false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        holder.postitemBinding.track.setText(postModals.get(position).getaName());
        holder.postitemBinding.artits.setText(postModals.get(position).getaArtist());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               post.selectted(postModals.get(position).getaPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }

    public class Myview extends RecyclerView.ViewHolder {
       private final SoundItemBinding postitemBinding;
        public Myview(SoundItemBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
