package com.social;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.social.services.Firebasedb;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Social extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
    public static Context sApplication;
    private static Context mContext;
    public static int screenWidth;
    public static int screenHeight;
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
       FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseApp.getApps(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/opensens.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        sApplication = getApplicationContext();
        mContext = this;
        DisplayMetrics mDisplayMetrics = getApplicationContext().getResources()
                .getDisplayMetrics();
        screenWidth = mDisplayMetrics.widthPixels;
        screenHeight = mDisplayMetrics.heightPixels;
        app = this;
//        startService(new Intent(mContext, Firebasedb.class));
    }
    public static Context getContext() {
        return mContext;
    }

    public static Social app;
    public static Social getInstance() {
        return app;
    }
}
