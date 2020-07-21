package com.social.adpaters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.social.Activity.Language;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.CommentSingleBinding;
import com.social.databinding.ItemlangBinding;
import com.social.model.Comment;
import com.social.model.User_selcetion;

import java.util.ArrayList;
import java.util.List;

public class Comment_Adpater extends RecyclerView.Adapter<Comment_Adpater.Myview> {
    LayoutInflater layoutInflater;
    Context context;
    ArrayList<Comment> postModals;
    Language interset;
    public Comment_Adpater(Context context, ArrayList<Comment> postModals) {
        this.context = context;
        this.postModals = postModals;
    }

    @NonNull
    @Override
    public Myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater ==null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        CommentSingleBinding postitemBinding= DataBindingUtil.inflate(layoutInflater, R.layout.comment_single,parent,false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        final Comment tabModal1=postModals.get(position);
        holder.postitemBinding.name.setText(tabModal1.getName());
        holder.postitemBinding.comment.setText(tabModal1.getComment());
        Utlity.Set_image(context,tabModal1.getPhoto(),holder.postitemBinding.user);
        holder.postitemBinding.name.setText(tabModal1.getName());


    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }

    public class Myview extends RecyclerView.ViewHolder {
       private final CommentSingleBinding postitemBinding;
        public Myview(CommentSingleBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
