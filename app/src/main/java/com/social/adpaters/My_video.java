package com.social.adpaters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.social.Activity.Video_play;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ItemgellaryBinding;
import com.social.model.Video;

import java.util.ArrayList;
import java.util.List;

public class My_video extends RecyclerView.Adapter<My_video.Myview>  implements Filterable {
    LayoutInflater layoutInflater;
    Context context;
    ArrayList<Video> postModals;
    ArrayList<Video> postModals2;
    HttpProxyCacheServer proxy;
    public My_video(Context context, ArrayList<Video> postModals, HttpProxyCacheServer proxy) {
        this.context = context;
        this.postModals = postModals;
        postModals2=new ArrayList<>(postModals);
        this.proxy=proxy;
    }

    @NonNull
    @Override
    public Myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemgellaryBinding postitemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.itemgellary, parent, false);
        return new Myview(postitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myview holder, final int position) {
        if(!TextUtils.isEmpty(postModals.get(position).getThumburl()))
       Utlity.Set_image(context,proxy.getProxyUrl(postModals.get(position).getThumburl()),holder.postitemBinding.videothumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Video_play.class).putExtra("post", Utlity.gson.toJson(postModals.get(position))));
            }
        });
    }

    @Override
    public int getItemCount() {
        return postModals.size();
    }
    @Override
    public Filter getFilter() {

        return searchfilter;
    }

    private Filter searchfilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Video> filterdlist = new ArrayList<>();

            if (constraint==null || constraint.length() ==0){
                filterdlist.addAll(postModals2);
            }
            else {
                String filterpattern =constraint.toString().toLowerCase().trim();

                for (Video item:postModals2){
                    if (item.getTitle().toLowerCase().contains(filterpattern)){
                        filterdlist.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterdlist;
            return  results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            postModals.clear();
            postModals.addAll((ArrayList) results.values);
            notifyDataSetChanged();


        }
    };

    public class Myview extends RecyclerView.ViewHolder {
        private final ItemgellaryBinding postitemBinding;

        public Myview(ItemgellaryBinding postitemBinding) {
            super(postitemBinding.getRoot());
            this.postitemBinding = postitemBinding;
        }
    }
}
