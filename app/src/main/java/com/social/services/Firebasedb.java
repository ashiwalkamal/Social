package com.social.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social.DB.DBHelper;
import com.social.Helpers.Utlity;
import com.social.Social;
import com.social.model.Video;

public class Firebasedb extends Service {
    private static final String TAG = "NotificationService";
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DBHelper mydb;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    public static Intent getIntent(Context packageContext) {
        return new Intent(packageContext, Firebasedb.class);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: NotificationService is being created...");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: NotificationService is being destroyed...");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
