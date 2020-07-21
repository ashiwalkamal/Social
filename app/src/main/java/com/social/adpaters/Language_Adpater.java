package com.social.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.social.Activity.Language;
import com.social.R;
import com.social.databinding.ItemintersetBinding;
import com.social.databinding.ItemlangBinding;
import com.social.model.User_selcetion;

import java.util.List;

public class Language_Adpater extends RecyclerView.Adapter<Language_Adpater.Myview> {
    LayoutInflater layoutInflater;
    Context context;
    List<User_selcetion> postModals;
    Language interset;
    public Language_Adpater(Context context, Language interset, List<User_selcetion> postModals) {
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
        ItemlangBinding postitemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.itemlang,parent,false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        final User_selcetion tabModal1=postModals.get(position);
        holder.postitemBinding.interset.setText(postModals.get(position).getName());
        holder.postitemBinding.language.setText(postModals.get(position).getLang());


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

    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }

    public class Myview extends RecyclerView.ViewHolder {
       private final ItemlangBinding postitemBinding;
        public Myview(ItemlangBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
