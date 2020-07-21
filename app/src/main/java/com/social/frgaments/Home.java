package com.social.frgaments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.reflect.TypeToken;
import com.social.Activity.Flag;
import com.social.Activity.Login;
import com.social.Activity.MainActivity;
import com.social.DB.DBHelper;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.adpaters.Comment_Adpater;
import com.social.adpaters.Videoadpater;
import com.social.databinding.FragmenthomeBinding;
import com.social.model.Comment;
import com.social.model.Follow;
import com.social.model.Like;
import com.social.model.Video;
import com.social.services.Method;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.social.Activity.MainActivity.firebaseUser;

public class Home extends Fragment {
    public static ArrayList<Video> posts = new ArrayList<>();
    public static Videoadpater videoadpater;
    public static ArrayList<Follow> follow = new ArrayList<>();
    public static ArrayList<Like> likes = new ArrayList<>();
    FragmenthomeBinding binding;
    String proxyUrl;
    HttpProxyCacheServer proxy;
    int lowerbound = 0;
    int upparbound = 3;
    int count = 0;
    int counte = 3;
    int lastPosition;
    AudioManager audioManager;
    FirebaseFirestore db;
    Query next;
    FirebaseFirestoreSettings settings;
    Query first;
    Query cooments;
    Query nextcommentnext;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference follow_ref;
    private DatabaseReference com;
    private DatabaseReference counter;
    private boolean is_loaded = false;
    private DBHelper mydb;
    private String oldestPostId;
    private boolean isPanelShown, iscomment;
    private Comment_Adpater adpater;
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<Comment> temp = new ArrayList<>();
    private String TAG = "firestore";
    private MediaPlayer myplay;
    private RecyclerView.LayoutManager commentmanger;
    private Method method;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmenthome, container, false);
        myplay = new MediaPlayer();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        method = new Method(getActivity());
        proxy = new HttpProxyCacheServer(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        if (firebaseUser != null) {
            Utlity.Set_image(getActivity(), String.valueOf(firebaseUser.getPhotoUrl()), binding.me);
            // to get all follow
            get_follow();
        }
        mydb = Utlity.connected(getActivity());
        //for comment
        adpater = new Comment_Adpater(getActivity(), temp);
        commentmanger = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        binding.list.setLayoutManager(commentmanger);
        binding.list.setAdapter(adpater);

        //for video
        videoadpater = new Videoadpater(getActivity(), posts, proxy, Home.this);
        binding.posts.setAdapter(videoadpater);


        //click listner
        click();
        binding.posts.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int index) {
                //to mange video
                manage_video(index);
            }
        });
        binding.closecomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_box();
            }
        });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    if (!Utlity.is_empty(binding.edtcomment.getText().toString()))
                        submit_comment();
                    else
                        binding.edtcomment.setError("Say Something !");
                } else
                    startActivity(new Intent(getActivity(), Login.class));
            }
        });

        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("-----", "end");
                    nextcommentnext.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                                        comments.add(document.toObject(Comment.class));
                                    }
                                    MainActivity.last_playing = 0;
                                    temp = get_video_comment();
                                    adpater.notifyDataSetChanged();
                                    // ...
                                }

                            });

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        return binding.getRoot();
    }

    private void submit_comment() {
        Comment comment = new Comment();
        comment.setUid(firebaseUser.getUid());
        comment.setId(posts.get(MainActivity.last_playing).getId());
        comment.setName(firebaseUser.getDisplayName());
        comment.setPhoto(String.valueOf(firebaseUser.getPhotoUrl()));
        comment.setComment(binding.edtcomment.getText().toString());
        temp.add(comment);
        comments.add(comment);
        adpater.notifyDataSetChanged();
        db.collection("user_comment").document(String.valueOf(System.currentTimeMillis())).set(comment);
        // mDatabase.child("user_comment").child("post_" + posts.get(MainActivity.last_playing).getId()).child(String.valueOf(System.currentTimeMillis())).setValue(comment);
        binding.edtcomment.setText("");
    }

    private void click() {
        database = FirebaseDatabase.getInstance();
        counter = database.getReference("counter");
        //to get number of item
        counter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                count = snapshot.getValue(Integer.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //binding.posts.// mPager is instance of viewpager
        //to load data
        load_data();
        //to get comments
        get_comment();

    }

    private void load_data() {
        posts.clear();
        first = db.collection("post")
                .orderBy("id")
                .limit(100);
        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                            posts.add(document.toObject(Video.class));
                        }
                        if (posts.size() > 0) {
                            MainActivity.last_playing = 0;
                            Video first_tab = posts.get(0);
                            // first_tab = posts.get(0);
                            if (firebaseUser != null) {
                                first_tab.setIsfollow(is_following(first_tab .getUserid()));
                                first_tab.setIslike(is_like(first_tab.getId()));

                            }
                            first_tab.setPlay(true);
                            posts.set(0, first_tab);

                            if (firebaseUser != null) {
                                likes();
                            } else {
                                videoadpater.notifyDataSetChanged();
                            }
                            binding.progress.setVisibility(View.GONE);
                            // ...
                            // Get the last visible document
                            DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() - 1);

                            // Construct a new query starting at this document,
                            // get the next 25 cities.
                            next = db.collection("post")
                                    .orderBy("id")
                                    .startAfter(lastVisible)
                                    .limit(100);

                            // Use the query for pagination
                            // ...
                        }


                    }

                });

    }

    @Override
    public void onPause() {
        if (MainActivity.last_playing != -1) {
            MainActivity.allready = true;
            Utlity.save_data(getActivity(), MainActivity.last_playing);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if ((MainActivity.last_playing != -1 && videoadpater != null) && (posts != null && posts.size() > MainActivity.last_playing)) {
            Video old = posts.get(MainActivity.last_playing);
            old.setPlay(true);
            posts.set(MainActivity.last_playing, old);
            videoadpater.notifyDataSetChanged();
        }
        super.onResume();
    }

    //to mange play back
    private void manage_video(int last_playing) {
        if (last_playing != -1) {
            if (last_playing != 0) {
                //mange previews video
                int oldin = last_playing - 1;
                if (posts.size() > oldin) {
                    Video old = posts.get(oldin);
                    old.setPlay(false);
                    posts.set(oldin, old);
                    //to mange current video
                }
                int secondlast = last_playing + 1;
                if (posts.size() > secondlast) {
                    Video oldo = posts.get(secondlast);
                    oldo.setPlay(false);
                    posts.set(secondlast, oldo);
                }

            } else {
                int oldin = last_playing + 1;
                Video old = posts.get(oldin);
                old.setPlay(false);
                posts.set(oldin, old);

            }
            Video current = posts.get(last_playing);
            current.setPlay(true);
            if (firebaseUser != null) {
                current.setIsfollow(is_following(current.getUserid()));
                current.setIslike(is_like(current.getId()));

            }
            posts.set(last_playing, current);
            binding.posts.setCurrentItem(last_playing);
            MainActivity.last_playing = last_playing;
        }
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        int last=posts.size()-1;
        if (last==last_playing && posts.size()< count) {
            next.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            posts.add(document.toObject(Video.class));
                        }
                        videoadpater.notifyDataSetChanged();
                        // Get the last visible document
                        if((task.getResult().size() - 1)!=-1) {
                            DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            // Construct a new query starting at this document,
                            // get the next 25 cities.
                            next = db.collection("post")
                                    .orderBy("id")
                                    .startAfter(lastVisible)
                                    .limit(100);
                        }
                    } else {
                        
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

        }
        else
        {
            videoadpater.notifyDataSetChanged();
        }
        //to get comments
        temp = get_video_comment();
        adpater.notifyDataSetChanged();



    }

    private void likes() {
        likes.clear();
        //com = database.getReference("user_post_like").child("post_" + posts.get(MainActivity.last_playing).getId());
        binding.progress.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection("user_post_like").document("post_" + posts.get(MainActivity.last_playing).getId() + firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        likes.add(document.toObject(Like.class));
                        if (firebaseUser != null && likes.size() == 1) {
                            if (is_like(posts.get(MainActivity.last_playing).getId())) {
                                Video video = posts.get(MainActivity.last_playing);
                                video.setIslike(true);
                                posts.set(MainActivity.last_playing, video);
                            }
                            videoadpater.notifyDataSetChanged();
                        }
                        else
                        {
                            videoadpater.notifyDataSetChanged();
                        }

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        videoadpater.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    videoadpater.notifyDataSetChanged();
                }
            }
        });

    }

    private boolean is_like(int id) {
        boolean is_like = false;
        for (Like like : likes) {
            if (like.getUid().equalsIgnoreCase(firebaseUser.getUid()) && like.getId() == id) {
                is_like = true;
                break;
            }
        }
        return is_like;
    }

    private ArrayList<Comment> get_video_comment() {
        temp.clear();
        for (Comment like : comments) {
            if (posts.size() > MainActivity.last_playing && like.getId() == posts.get(MainActivity.last_playing).getId()) {
                temp.add(like);
            }
        }
        return temp;
    }

    private boolean is_following(String id) {
        boolean is_fllow = false;
        for (Follow like : follow) {
            if (like.getFollwings().equalsIgnoreCase(id) && like.getFollwer().equalsIgnoreCase(firebaseUser.getUid())) {
                is_fllow = true;
                break;
            }
        }
        return is_fllow;
    }


    private void get_comment() {
        binding.progress.setVisibility(View.VISIBLE);

        comments.clear();
        adpater.notifyDataSetChanged();
        cooments = db.collection("user_comment")
                .limit(50);
        cooments.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                            comments.add(document.toObject(Comment.class));
                        }
                        MainActivity.last_playing = 0;
                        temp = get_video_comment();
                        adpater.notifyDataSetChanged();
                        binding.progress.setVisibility(View.GONE);
                        // Use the query for pagination
                        if (documentSnapshots.getDocuments().size() > 0) {
                            DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() - 1);
                            nextcommentnext = db.collection("user_comment")
                                    .startAfter(lastVisible)
                                    .limit(50);
                        }

                        // ...
                    }

                });
    }

    private void get_follow() {
        follow.clear();
        // follow_ref = database.getReference("user_follow");
        binding.progress.setVisibility(View.VISIBLE);

        Query first = db.collection("user_follow");
        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                            follow.add(document.toObject(Follow.class));
                        }
                        binding.progress.setVisibility(View.GONE);
                    }

                });
    }


    public void follow(String user_id) {
        if (firebaseUser != null) {
            if (firebaseUser.getUid().equalsIgnoreCase(user_id)) {
                Utlity.show_toast(getActivity(), getString(R.string.selffllow));
            } else {
                Follow follow = new Follow();
                follow.setFollwer(firebaseUser.getUid());
                follow.setFollwings(user_id);
                db.collection("user_follow").document(String.valueOf(System.currentTimeMillis())).set(follow);
                // mDatabase.child("user_follow").child(firebaseUser.getUid() + "_" + System.currentTimeMillis()).setValue(follow);
                Video video = posts.get(MainActivity.last_playing);
                video.setIsfollow(true);
                videoadpater.notifyDataSetChanged();
                posts.set(MainActivity.last_playing, video);

                Utlity.show_toast(getActivity(), getString(R.string.fllowto) + posts.get(MainActivity.last_playing).getName());
            }
        } else {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }

    public void like(Video videouser) {
        if (firebaseUser != null) {
            Like like = new Like();
            like.setUid(firebaseUser.getUid());
            like.setId(videouser.getId());
            db.collection("user_post_like").document("post_" + videouser.getId() + firebaseUser.getUid()).set(like);
            // mDatabase.child("user_post_like").child("post_" + posts.get(MainActivity.last_playing).getId()).child(String.valueOf(System.currentTimeMillis())).setValue(like);
            likes.add(like);

            Video video = posts.get(MainActivity.last_playing);
            video.setIslike(true);
            posts.set(MainActivity.last_playing, video);
            videoadpater.notifyDataSetChanged();
            //   binding.posts.setCurrentItem(MainActivity.last_playing);
        } else {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }


    public void comment_box() {
        if (!iscomment) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.bottom_up);

            binding.llcomment.startAnimation(bottomUp);
            binding.llcomment.setVisibility(View.VISIBLE);
            iscomment = true;
        } else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.bottom_down);

            binding.llcomment.startAnimation(bottomDown);
            binding.llcomment.setVisibility(View.INVISIBLE);
            Utlity.hideKeyboard(getActivity());
            iscomment = false;
        }
    }

    public void share(Video videourl) {
        if (Method.isNetworkAvailable(getActivity())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)  {
                        method.download(String.valueOf(videourl.getId()),videourl.getVideourl());
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
                else
                {
                    method.download(String.valueOf(videourl.getId()),videourl.getVideourl());
                }
        } else {
            method.alertBox(getResources().getString(R.string.nointerenet));
        }


    }


    public void flag(Video video) {
        if(firebaseUser!=null) {
            startActivity(new Intent(getActivity(), Flag.class).putExtra("video", Utlity.gson.toJson(video)));
        }
        else
        {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }
}
