package com.social.adpaters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.social.Activity.Final_step;
import com.social.Activity.Interset;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ItemhashBinding;
import com.social.databinding.ItemintersetBinding;
import com.social.model.User_selcetion;

import java.util.ArrayList;
import java.util.List;

public class Hash_Adpater extends RecyclerView.Adapter<Hash_Adpater.Myview> {
    LayoutInflater layoutInflater;
    Context context;
    ArrayList<String> postModals;
    Final_step interset;
    public Hash_Adpater(Context context, Final_step interset, ArrayList<String> postModals) {
        this.context = context;
        this.postModals = postModals;
        this.interset=interset;
    }

    @NonNull
    @Override
    public Myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater ==null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemhashBinding postitemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.itemhash,parent,false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        holder.postitemBinding.hashtag.setText(postModals.get(position));
        holder.postitemBinding.tag.setBackgroundColor(Color.parseColor(Utlity.generateColor()));

    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }

    public class Myview extends RecyclerView.ViewHolder {
       private final ItemhashBinding postitemBinding;
        public Myview(ItemhashBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
