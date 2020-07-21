package com.social.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.model.Video;

public class Splesh extends Activity {

    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utlity.full_screen(this);
        setContentView(R.layout.activity_splesh);
        t=new Thread(){
            public void run()
            {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    if(!Utlity.get_first(Splesh.this)) {
                        Utlity.user_first(Splesh.this);
                        startActivity(new Intent(Splesh.this, Gender.class));
                        finishAffinity();
                    }
                    else
                    {
                        startActivity(new Intent(Splesh.this, MainActivity.class));
                        finishAffinity();
                    }
                }
            }
        };t.start();
    }


}