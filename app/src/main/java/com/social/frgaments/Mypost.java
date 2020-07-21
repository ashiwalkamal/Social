package com.social.frgaments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.social.Activity.MainActivity;
import com.social.Activity.Post;
import com.social.R;
import com.social.adpaters.Gallery_video;
import com.social.adpaters.My_video;
import com.social.databinding.FragmenthomeBinding;
import com.social.databinding.FragmentpostBinding;
import com.social.model.Video;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Mypost extends Fragment {
    FragmentpostBinding binding;
    My_video my_video;
    ArrayList<Video> mypost=new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    HttpProxyCacheServer proxy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmentpost, container, false);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        mAuth = instance;
        firebaseUser = mAuth.getCurrentUser();
        proxy = new HttpProxyCacheServer(getApplicationContext());
        binding.posts.setLayoutManager(new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false));
        my_video = new My_video(getActivity(),  mypost,proxy);
        binding.posts.setAdapter(my_video);
        for(Video video:Home.posts)
        {
            if(video.getUserid().equalsIgnoreCase(firebaseUser.getUid()))
            {
                mypost.add(video);
                my_video.notifyDataSetChanged();
            }
        }

        return binding.getRoot();
    }

}
