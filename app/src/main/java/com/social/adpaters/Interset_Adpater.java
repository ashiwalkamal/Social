package com.social.adpaters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.social.Activity.Interset;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ItemintersetBinding;
import com.social.model.Interset_selcetion;
import com.social.model.User_selcetion;

import java.util.List;

public class Interset_Adpater extends RecyclerView.Adapter<Interset_Adpater.Myview> {
    LayoutInflater layoutInflater;
    Context context;
    List<Interset_selcetion> postModals;
    Interset interset;
    public Interset_Adpater(Context context, Interset interset, List<Interset_selcetion> postModals) {
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
        ItemintersetBinding postitemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.iteminterset,parent,false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        final Interset_selcetion tabModal1=postModals.get(position);
        holder.postitemBinding.interset.setText(postModals.get(position).getName());
        Utlity.Set_image(context,tabModal1.getIcon(),holder.postitemBinding.icons);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(tabModal1.isIs_selected()) {
                tabModal1.setIs_selected(false);
            }
            else
            {
                tabModal1.setIs_selected(true);
            }
            interset.handle_click(position,tabModal1);
            }
        });
        if(tabModal1.isIs_selected()) {
            holder.postitemBinding.box.setBackground(context.getResources().getDrawable(R.drawable.shape_circle));
            holder.postitemBinding.interset.setTextColor(Color.parseColor("#5640B8"));
        }
        else
        {
            holder.postitemBinding.box.setBackground(context.getResources().getDrawable(R.drawable.shape_circle_active));
            holder.postitemBinding.interset.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }

    public class Myview extends RecyclerView.ViewHolder {
       private final ItemintersetBinding postitemBinding;
        public Myview(ItemintersetBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
