package com.social.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.social.R;

public class Successupload extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successupload);

    }

    public void home(View view) {
        startActivity(new Intent(this,MainActivity.class));
        finishAffinity();
    }
}