package com.social.Activity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.social.R;
import com.social.adpaters.Interset_Adpater;
import com.social.databinding.ActivityIntersetBinding;
import com.social.model.Interset_selcetion;
import com.social.model.User_selcetion;

import java.util.ArrayList;

public class Interset extends Activity {
    ActivityIntersetBinding binding;
    ArrayList<Interset_selcetion> inersets;
    Interset_Adpater adpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_interset);

        inersets=new ArrayList<>();
        inersets.add(new Interset_selcetion(false,"Game","https://img.favpng.com/6/22/23/game-controllers-video-games-logo-black-vector-graphics-png-favpng-kTtFCDp6WufXf0GRRHgCpKixm.jpg"));
        inersets.add(new Interset_selcetion(false,"Social","https://img.favpng.com/15/9/17/social-media-marketing-social-network-advertising-png-favpng-thSvQdM9E93LaehGP48BQPWit.jpg"));
        inersets.add(new Interset_selcetion(false,"Mobile","https://mpng.subpng.com/20180311/kbe/kisspng-mobile-phone-telephone-mobile-app-mobile-phones-and-people-vector-material-5aa57df585ef94.0728878915207951255486.jpg"));
        inersets.add(new Interset_selcetion(false,"Craft","https://www.pinclipart.com/picdir/middle/30-301934_png-transparent-download-artist-vector-craft-clipart.png"));
        inersets.add(new Interset_selcetion(false,"Music","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTHrgZj800TSnuTCTtEjHYqjKK34fgANFPtp60tYHVFjdb0Tb7tluUByHlATVgn3r_SmLpdN1dmGPmCkQ&usqp=CAU"));
        inersets.add(new Interset_selcetion(false,"Comedy","https://img.favpng.com/10/16/5/vector-graphics-illustration-royalty-free-computer-icons-comedian-png-favpng-Hp5ZjdgwjGLPqARSU1u8me8Y7.jpg"));
        inersets.add(new Interset_selcetion(false,"Science","https://cdn.imgbin.com/6/0/21/imgbin-laboratory-science-chemistry-computer-lab-technology-science-cVkyKKPKu9dgK7b6Cu3H4xYfR.jpg"));
        inersets.add(new Interset_selcetion(false,"Love","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTdwShTbbjcihnEm8e2_7GKX-XKWJhmjdSKZQ&usqp=CAU"));
        inersets.add(new Interset_selcetion(false,"Couple","https://www.clipartmax.com/png/middle/40-401740_wedding-couple-groom-bride-pictures-png-images-wedding-couple-vector-png.png"));
        inersets.add(new Interset_selcetion(false,"Dating","https://www.clipartmax.com/png/middle/237-2377896_speed-dating-and-uncovering-supply-chain-challenges-speed-dating-png.png"));


        binding.interset.setLayoutManager(new GridLayoutManager(this,4, RecyclerView.VERTICAL, false));
        adpater = new Interset_Adpater(this, Interset.this,inersets);
        binding.interset.setAdapter(adpater);

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Interset.this,MainActivity.class));
                finish();
            }
        });

    }
    public void handle_click(int pos, Interset_selcetion user_selcetion)
    {
        inersets.set(pos, user_selcetion);
        adpater.notifyDataSetChanged();
    }
}