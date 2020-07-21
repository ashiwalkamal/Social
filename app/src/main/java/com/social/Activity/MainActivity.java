package com.social.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ActivityMainBinding;
import com.social.frgaments.Discover;
import com.social.frgaments.Home;
import com.social.frgaments.Live;
import com.social.frgaments.Profile;
import com.social.model.Follow;
import com.social.model.Like;
import com.social.model.Video;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.social.frgaments.Home.posts;
import static com.social.frgaments.Home.videoadpater;

public class MainActivity extends AppCompatActivity {
    public static ActivityMainBinding binding;
    public static int last_playing = -1;
    public static boolean allready = false;
    public static FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    private FragmentTransaction transaction;
    private FragmentManager mFragmentManager;
    private boolean is_lunched = false;
    public static  ArrayList<String> gallery = new ArrayList<>();
    public  static  MainActivity mainActivity;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(new Home(), getResources().getString(R.string.home));
                    home_swip();
                    return true;
                case R.id.navigation_discover:
                    loadFragment(new Discover(), "Discovar");
                    handle_fragment_swip();
                    return true;
                case R.id.navigation_add:
                    if (firebaseUser != null) {
                        loadFragment(new Post(), "post");
                    } else {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                    handle_fragment_swip();
                    return true;

                case R.id.navigation_live:
                    loadFragment(new Live(), "live");
                    handle_fragment_swip();
                    return true;

                case R.id.navigation_user:
                    if (firebaseUser != null) {
                        loadFragment(new Profile(), "profile");
                    } else {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                    handle_fragment_swip();
                    return true;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity=this;
        FirebaseAuth instance = FirebaseAuth.getInstance();
        mAuth = instance;
        mFragmentManager = getSupportFragmentManager();
        firebaseUser = mAuth.getCurrentUser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.navview.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        binding.navview.setSelectedItemId(R.id.navigation_home);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        
        isStoragePermissionGranted();

    }


    public void loadFragment(Fragment fragment, String tag) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment currentFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tag);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.frame, fragmentTemp, tag);
        } else {
            fragmentTransaction.show(fragmentTemp);
            if(Profile.binding!=null) {
                Profile.binding.fllowrs.setText(Utlity.formatK(get_fllower()));
                Profile.binding.foolwings.setText(Utlity.formatK(get_flloweing()));
                Profile.binding.likes.setText(Utlity.formatK(get_likes()));
                Profile.adapter.notifyDataSetChanged();
            }
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();

    }


    public void handle_fragment_swip() {
        if (MainActivity.last_playing != -1 && videoadpater != null && posts != null && posts.size() > MainActivity.last_playing) {

            Video old = Home.posts.get(MainActivity.last_playing);
            old.setPlay(false);
            Home.posts.set(MainActivity.last_playing, old);
            Home.videoadpater.notifyDataSetChanged();
            Utlity.save_data(this, MainActivity.last_playing);
        }
    }

    public void home_swip() {
        if (MainActivity.last_playing != -1 && videoadpater != null && posts != null && posts.size() > MainActivity.last_playing) {
            Video old = Home.posts.get(MainActivity.last_playing);
            old.setPlay(true);
            Home.posts.set(MainActivity.last_playing, old);
            Home.videoadpater.notifyDataSetChanged();

            Utlity.save_data(this, MainActivity.last_playing);

        }
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 10) {
            FirebaseAuth instance = FirebaseAuth.getInstance();
            mAuth = instance;
            firebaseUser = mAuth.getCurrentUser();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.exitmsg);
        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                new AsyncTaskExample().execute();
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            if (firebaseUser != null) {
                loadFragment(new Post(), "post");
            } else {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
            handle_fragment_swip();
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   new AsyncTaskExample().execute();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Utlity.show_toast(this, "Permission denied to read your External storage");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private class AsyncTaskExample extends AsyncTask<String, String, Boolean> {
        @SuppressLint({"Recycle", "WrongThread"})
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        protected Boolean doInBackground(String... strings) {
            int int_position = 0;
            Uri uri;
            Cursor cursor;
            int column_index_data, thum;

            String absolutePathOfImage = null;
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

            String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                gallery.add(absolutePathOfImage);

            }
            return true;
        }

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
