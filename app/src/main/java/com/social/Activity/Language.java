package com.social.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.social.R;
import com.social.adpaters.Interset_Adpater;
import com.social.adpaters.Language_Adpater;
import com.social.databinding.ActivityLanguageBinding;
import com.social.model.User_selcetion;

import java.util.ArrayList;

public class Language extends Activity {
    ActivityLanguageBinding binding;
    ArrayList<User_selcetion> lanuage;
    Language_Adpater adpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_language);
        lanuage=new ArrayList<>();
        lanuage.add(new User_selcetion(false,"English","English"));
        lanuage.add(new User_selcetion(false,"Bengali","বাংলা"));
        lanuage.add(new User_selcetion(false,"Gujarati","ગુજરાતી"));
        lanuage.add(new User_selcetion(false,"Punjabi","ਪੰਜਾਬੀ"));
        lanuage.add(new User_selcetion(false,"Rajasthani","राजस्थानी"));
        lanuage.add(new User_selcetion(false,"Francis","Francis"));
        lanuage.add(new User_selcetion(false,"Hindi","हिन्दी"));
        lanuage.add(new User_selcetion(false,"Malay","Bahasa Melayu"));
        lanuage.add(new User_selcetion(false,"Marathi","मराठी"));
        lanuage.add(new User_selcetion(false,"Indonesians","Orang indonesia"));

        binding.language.setLayoutManager(new GridLayoutManager(this,2, RecyclerView.VERTICAL, false));
        adpater = new Language_Adpater(this, Language.this,lanuage);
        binding.language.setAdapter(adpater);

        binding.btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Language.this,Interset.class));
                finish();
            }
        });

    }

    public void handle_click(int position, User_selcetion tabModal1) {
        lanuage.set(position,tabModal1);
        adpater.notifyDataSetChanged();
    }
}