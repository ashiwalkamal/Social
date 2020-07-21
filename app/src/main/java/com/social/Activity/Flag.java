package com.social.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.adpaters.Interset_Adpater;
import com.social.adpaters.Reason_Adpater;
import com.social.databinding.ActivityFlagBinding;
import com.social.model.Interset_selcetion;
import com.social.model.Report;
import com.social.model.Video;

import java.util.ArrayList;


public class Flag extends AppCompatActivity implements View.OnClickListener {
    ActivityFlagBinding binding;
    FirebaseFirestore db;
    Video video;
    String reason = "";
    boolean is_first = false;
    ArrayList<String> reasons;
    Reason_Adpater adpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_flag);
        db = FirebaseFirestore.getInstance();
        if (getIntent() != null) {
            video = Utlity.gson.fromJson(getIntent().getStringExtra("video"), Video.class);
        }
        //find
        find();
    }

    private void find() {

        reasons=new ArrayList<>();
        reasons.add("Plagarism");
        reasons.add("Criminal Activity");
        reasons.add("Nudity");
        reasons.add("Spam");
        reasons.add("Hate Speech");
        reasons.add("Disturbing content");
        reasons.add("Other");


        binding.reasonlist.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adpater = new Reason_Adpater(this, Flag.this,reasons);
        binding.reasonlist.setAdapter(adpater);


        binding.submit.setOnClickListener(this);
        binding.close.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (Utlity.is_online(this)) {
                    if (!TextUtils.isEmpty(binding.issue.getText().toString()))
                        submit(reason, binding.issue.getText().toString());
                    else
                        Utlity.show_toast(this, "Please enter your issue !");
                } else {
                    Utlity.show_toast(this, "No Internet !");
                }
                break;
            case R.id.close:
                if (is_first) {
                    binding.reasonlist.setVisibility(View.VISIBLE);
                    binding.issue.setVisibility(View.GONE);
                    binding.submit.setVisibility(View.GONE);
                    is_first = false;
                } else {
                    finish();
                }
                break;
        }
    }

    private void submit(String reasong, String comment) {

        if (db != null) {
            Utlity.show_progress(this);
            Report report = new Report();
            report.setUid(MainActivity.firebaseUser.getUid());
            report.setReason(reasong);
            report.setComment(comment);
            report.setVid(video.getId());
            report.setName(MainActivity.firebaseUser.getDisplayName());
            /// mDatabase.child("user_post").child(firebaseUser.getUid()+"_"+System.currentTimeMillis()).setValue(video);
            db.collection("report").document(String.valueOf(System.currentTimeMillis()))
                    .set(report)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Utlity.show_toast(Flag.this, "Your Report Submit success ");

                            Utlity.dismiss_dilog(Flag.this);
                            MainActivity.allready = false;
                            MainActivity.binding.navview.setSelectedItemId(R.id.navigation_home);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utlity.dismiss_dilog(Flag.this);
                            Utlity.show_alert(Flag.this, e.getMessage(), false);
                        }
                    });

        }
    }

    public void handle_click(String tabModal1) {
        reason = tabModal1;
        binding.reasonlist.setVisibility(View.GONE);
        binding.issue.setVisibility(View.VISIBLE);
        binding.submit.setVisibility(View.VISIBLE);
        is_first = true;
    }

    @Override
    public void onBackPressed() {
        if (is_first) {
            binding.reasonlist.setVisibility(View.VISIBLE);
            binding.issue.setVisibility(View.GONE);
            binding.submit.setVisibility(View.GONE);
            is_first = false;
        } else {
            finish();
        }
    }
}