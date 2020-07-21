package com.social.frgaments;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.social.R;
import com.social.adpaters.My_video;
import com.social.databinding.FragmentdiscovarBinding;
import com.social.databinding.FragmenthomeBinding;
import com.social.model.Video;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Discover extends Fragment {
    FragmentdiscovarBinding binding;
    String proxyUrl;
    HttpProxyCacheServer proxy;
    My_video my_video;
    ArrayList<Video> mypost=new ArrayList<>();
    FirebaseUser firebaseUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmentdiscovar, container, false);
        proxy = new HttpProxyCacheServer(getApplicationContext());
        binding.videos.setLayoutManager(new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false));
        my_video = new My_video(getActivity(),  Home.posts,proxy);
        binding.videos.setAdapter(my_video);

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                my_video.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return binding.getRoot();
    }


}
