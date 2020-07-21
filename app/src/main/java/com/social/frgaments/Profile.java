package com.social.frgaments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.social.Activity.MainActivity;
import com.social.Activity.Myaccount;
import com.social.Activity.Settings;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.adpaters.Profile_tabadpater;
import com.social.databinding.FragmentprofileBinding;
import com.social.model.Follow;
import com.social.model.Like;

import java.util.ArrayList;

public class Profile extends Fragment implements View.OnClickListener {
    public static FragmentprofileBinding binding;
    static Context profile;
    public static  Profile_tabadpater adapter;
    ArrayList<String> pic = new ArrayList<>();
    StorageReference storageReference;
    FirebaseStorage storage;
    String photo = "";
    private int RESULT_LOAD_IMAGE1 = 1;

    public static void change_profile() {
        //set profile
        if (MainActivity.firebaseUser != null) {
            if (!TextUtils.isEmpty(MainActivity.firebaseUser.getDisplayName())) {
                binding.name.setText(MainActivity.firebaseUser.getDisplayName());
                binding.dname.setText(MainActivity.firebaseUser.getDisplayName());
            } else {
                binding.name.setText("User" + Utlity.getRandomNumber(1, 1000000));
                binding.dname.setText("User" + Utlity.getRandomNumber(1, 1000000));
            }
            Utlity.Set_image(profile, String.valueOf(MainActivity.firebaseUser.getPhotoUrl()), binding.userphoto);
            Utlity.Set_image(profile, String.valueOf(MainActivity.firebaseUser.getPhotoUrl()), binding.covar);
        }
    }
    private int[] tabIcons = {
            R.drawable.ic_film,
            R.drawable.ic_hash_tag,
            R.drawable.ic_add
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmentprofile, container, false);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profile = getContext();
        //set profile
        if (MainActivity.firebaseUser != null) {
            setprofile(MainActivity.firebaseUser);
        }
        binding.setting.setOnClickListener(this);
        binding.userphoto.setOnClickListener(this);
        binding.dname.setOnClickListener(this);

        binding.tabLayout.addTab(binding.tabLayout.newTab());
        binding.tabLayout.addTab(binding.tabLayout.newTab());
        binding.tabLayout.addTab(binding.tabLayout.newTab());
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        adapter = new Profile_tabadpater(getActivity(), getFragmentManager(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        setupTabIcons();
        return binding.getRoot();
    }

    public void setprofile(FirebaseUser firebaseUser) {
        if (!TextUtils.isEmpty(firebaseUser.getDisplayName())) {
            binding.name.setText(firebaseUser.getDisplayName());
            binding.dname.setText(firebaseUser.getDisplayName());
        } else {
            binding.name.setText("User" + Utlity.getRandomNumber(1, 1000000));
            binding.dname.setText("User" + Utlity.getRandomNumber(1, 1000000));
        }
        Utlity.Set_image(getActivity(), String.valueOf(firebaseUser.getPhotoUrl()), binding.userphoto);
        Utlity.Set_image(getActivity(), String.valueOf(firebaseUser.getPhotoUrl()), binding.covar);

        binding.fllowrs.setText(Utlity.formatK(get_fllower()));
        binding.foolwings.setText(Utlity.formatK(get_flloweing()));
        binding.likes.setText(Utlity.formatK(get_likes()));


    }

    private void setupTabIcons() {
        binding.tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        binding.tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        binding.tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                startActivity(new Intent(getActivity(), Settings.class));
                break;
            case R.id.userphoto:
                startActivity(new Intent(getActivity(), Myaccount.class));
                break;
        }
    }

    @Override
    public void onResume() {
        if (MainActivity.firebaseUser != null) {
            setprofile(MainActivity.firebaseUser);
        }
        super.onResume();
    }

    private int get_likes() {
        int i = 0;
        for (Like like : Home.likes) {
            if (like.getUid().equalsIgnoreCase(MainActivity.firebaseUser.getUid())) {
                i++;
            }

        }
        return i;
    }

    private int get_flloweing() {
        int i = 0;
        for (Follow like : Home.follow) {
            if (like.getFollwings().equalsIgnoreCase(MainActivity.firebaseUser.getUid())) {
                i++;
            }
        }
        return i;
    }

    private int get_fllower() {
        int i = 0;
        for (Follow like : Home.follow) {
            if (like.getFollwer().equalsIgnoreCase(MainActivity.firebaseUser.getUid())) {
                i++;
            }
        }
        return i;
    }
}
