package com.social.frgaments;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.danikula.videocache.HttpProxyCacheServer;
import com.social.Activity.MainActivity;
import com.social.R;
import com.social.databinding.FragmenthomeBinding;
import com.social.databinding.FragmentliveBinding;

public class Live extends Fragment {
    FragmentliveBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmentlive, container, false);

        return binding.getRoot();
    }


}
