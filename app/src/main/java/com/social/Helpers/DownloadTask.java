package com.social.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.social.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

public class DownloadTask {
    private static final int REQUEST = 112;
    private static final String TAG = "Download Task";
    StringTokenizer st;
    Activity activity;
    private Context context;
    private Button buttonText;
    private String downloadUrl = "", downloadFileName = "";
    private ProgressDialog pd;

    private boolean is_share;

    public DownloadTask(Activity activity, String downloadUrl) {
        this.context = context;
        this.buttonText = buttonText;
        this.downloadUrl = downloadUrl;
        this.activity = activity;
        String[] separated = downloadUrl.split("http://shyamsarkar.in/sarkaradmin/assets/live_darshan/");
        try {
            downloadFileName = separated[1];
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
        Log.e(TAG, downloadFileName);

        File direct = new File(Environment.getExternalStorageDirectory() + "/shyamsarkar/video/");
        //If File is not present create directory
        if (!direct.exists()) {
            direct.mkdir();
            Log.e(TAG, "Directory Created.");
        }

       File outputFile = new File(direct, downloadFileName);//Create Output file in Main File
        //Create New File if not present
        if (!outputFile.exists()) {
            new DownloadingTask().execute();
        }
        else
        {
            Uri uriPath = Uri.parse(Environment.getExternalStorageDirectory() + "/shyamsarkar/video/" + downloadFileName);
            Utlity.share_video(activity,uriPath);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            create_notifcation(activity);
            //Set Button Text when download started
            pd = new ProgressDialog(activity);
            pd.setMessage("Please Wait...");
            pd.show();
            pd.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection
                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }

                File direct = new File(Environment.getExternalStorageDirectory() + "/shyamsarkar/video/");
                if (!direct.exists()) {
                    direct.mkdirs();
                    Log.e(TAG, "Directory Created.");
                }
                outputFile = new File(direct, downloadFileName);//Create Output file in Main File
                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }


                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {
                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            try {
                if (outputFile != null ) {
                    Uri uriPath = Uri.parse(Environment.getExternalStorageDirectory() + "/shyamsarkar/video/" + downloadFileName);
                    Utlity.share_video(activity, uriPath);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }
            super.onPostExecute(result);
        }

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void create_notifcation(Context context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        final int icon = R.mipmap.ic_launcher;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getPackageName() + "/raw/notification");

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker("Shyam Darbar").setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("Shyam Darbar")
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setChannelId("")
                .setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Video Status Download successfully...")
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }
}
