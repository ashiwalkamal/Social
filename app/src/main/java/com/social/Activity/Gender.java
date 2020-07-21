package com.social.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.social.R;
import com.social.databinding.ActivityGenderBinding;

public class Gender extends Activity implements View.OnClickListener {

    ActivityGenderBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_gender);
       //click
        click();
    }

    private void click() {
        binding.one.setOnClickListener(this);
        binding.two.setOnClickListener(this);
        binding.three.setOnClickListener(this);
        binding.oner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        binding.tewor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        binding.threer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.one:
              next();
                break;
            case R.id.two:
               next();
                break;
            case R.id.three:
                next();
                break;
        }
    }

    private void next() {
        startActivity(new Intent(Gender.this,Language.class));
    }
}