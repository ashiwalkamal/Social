package com.social.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


import com.social.R;
import com.social.Social;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.List;

public class Method {

    public static Activity activity;
    public static boolean loginBack = false, allowPermitionExternalStorage = false;
    public static boolean isUpload = true, isDownload = true;
    public boolean personalization_ad = false;
    public static String search_title;


    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "login";
    public String pref_login = "pref_login";
    private String firstTime = "firstTime";
    public String profileId = "profileId";
    public String userEmail = "userEmail";
    public String userPassword = "userPassword";
    public String userName = "userName";
    public String userImage = "userImage";
    public String loginType = "loginType";
    public String show_login = "show_login";
    public String notification = "notification";
    public String verification_code = "verification_code";
    public String is_verification = "is_verification";

    public String reg_name = "reg_name";
    public String reg_email = "reg_email";
    public String reg_password = "reg_password";
    public String reg_phoneNo = "reg_phoneNo";
    public String reg_reference = "reg_reference";

    private String filename;
    private String storageFile;


    @SuppressLint("StaticFieldLeak")
    public static Activity activity_upload;

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity) {
        this.activity = activity;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


    public void login() {
        if (!pref.getBoolean(firstTime, false)) {
            editor.putBoolean(pref_login, false);
            editor.putBoolean(firstTime, true);
            editor.commit();
        }
    }



    //Whatsapp application installation or not check
    public boolean isAppInstalled_Whatsapp(Activity activity) {
        String packageName = "com.whatsapp";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    //instagram application installation or not check
    public boolean isAppInstalled_Instagram(Activity activity) {
        String packageName = "com.instagram.android";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    //facebook application installation or not check
    public boolean isAppInstalled_facebook(Activity activity) {
        String packageName = "com.facebook.katana";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    //twitter application installation or not check
    public boolean isAppInstalled_twitter(Activity activity) {
        String packageName = "com.twitter.android";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        } else {
            return false;
        }
    }

    //network check
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //get screen width
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    //get screen height
    public int getScreenHeight() {
        int columnHeight;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnHeight = point.y;
        return columnHeight;
    }


    //---------------Download status video---------------//
    public void download(String video_id,  String video_uri) {

        storageFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/social/video/" + "filename-" + video_id + ".mp4";
        File file = new File(storageFile);
        if (!file.exists()) {

            Method.isDownload = false;

            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/social/video/");
            if (!root.exists()) {
                root.mkdirs();
            }

            filename = "filename-" + video_id + ".mp4";

            Intent serviceIntent = new Intent(activity, DownloadService.class);
            serviceIntent.setAction(DownloadService.ACTION_START);
            serviceIntent.putExtra("video_id", video_id);
            serviceIntent.putExtra("downloadUrl", video_uri);
            serviceIntent.putExtra("file_path", root.toString());
            serviceIntent.putExtra("file_name", filename);
            activity.startService(serviceIntent);


        } else {
            filename = "filename-" + video_id + ".mp4";

            showMedia(storageFile);
        }

       // new DownloadImage().execute(video_image, video_id, video_category_id, video_name, category, layout_type);

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImage extends AsyncTask<String, String, String> {

        private String video_id, video_category_id, video_name, category, layout_type;
        Bitmap bitmapDownload;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                video_id = params[1];
                video_category_id = params[2];
                video_name = params[3];
                category = params[4];
                layout_type = params[5];
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmapDownload = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                // Log exception
                Log.w("error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            downloadImage(bitmapDownload, video_id, video_category_id, video_name, category, layout_type);

            super.onPostExecute(s);
        }

    }

    private void downloadImage(Bitmap bitmap, String video_id, String video_category_id, String video_name, String category, String layout_type) {

        String filePath = null;

        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/social/";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }

        String fname = "Image-" + video_id;
        filePath = iconsStoragePath + fname + ".jpg";
        File file = new File(iconsStoragePath, filePath);
        if (file.exists()) {
            Log.d("file_exists", "file_exists");
        } else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

                //choose another format if PNG doesn't suit you
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                bos.flush();
                bos.close();

            } catch (FileNotFoundException e) {
                Log.w("TAG", "Error saving image file: " + e.getMessage());
            } catch (IOException e) {
                Log.w("TAG", "Error saving image file: " + e.getMessage());
            }
        }


    }
    //---------------Download status video---------------//



    //alert message box
    public void alertBox(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(Html.fromHtml(message));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(activity.getResources().getString(R.string.pix_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    //alert message box
    public void suspend(String message){

        if(pref.getBoolean(pref_login,false)){
            editor.putBoolean(pref_login, false);
            editor.commit();
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(Html.fromHtml(message));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(activity.getResources().getString(R.string.pix_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    //view count and user video like format
    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public static void showMedia(String file_path) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Share post via social text demo");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file_path));
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        activity.startActivity(Intent.createChooser(shareIntent, "Share post"));

    }

}
