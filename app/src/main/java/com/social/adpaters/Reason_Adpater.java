package com.social.adpaters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.social.Activity.Flag;
import com.social.Activity.Interset;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ItemFlagBinding;
import com.social.databinding.ItemintersetBinding;
import com.social.model.Interset_selcetion;

import java.util.List;

public class Reason_Adpater extends RecyclerView.Adapter<Reason_Adpater.Myview> {
    LayoutInflater layoutInflater;
    Context context;
    List<String> postModals;
    Flag interset;
    public Reason_Adpater(Context context, Flag interset, List<String> postModals) {
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
        ItemFlagBinding postitemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.item_flag,parent,false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        final String tabModal1=postModals.get(position);
        holder.postitemBinding.reason.setText(tabModal1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            interset.handle_click(tabModal1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }

    public class Myview extends RecyclerView.ViewHolder {
       private final ItemFlagBinding postitemBinding;
        public Myview(ItemFlagBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
