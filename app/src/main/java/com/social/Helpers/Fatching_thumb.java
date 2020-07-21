package com.social.Helpers;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.danikula.videocache.HttpProxyCacheServer;
import com.social.R;

import java.util.HashMap;

public class Fatching_thumb extends AsyncTask<String, String, Bitmap> {
    Bitmap bitmap = null;
    ImageView imageView;
    ProgressBar ba;
    HttpProxyCacheServer proxy;
    public Fatching_thumb(ImageView thumb,ProgressBar progressBar) {
        this.imageView = thumb;
        this.ba=progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ba.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap myBitmap = null;
        MediaMetadataRetriever mMRetriever = null;
        try {
            mMRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mMRetriever.setDataSource(strings[0], new HashMap<String, String>());
            else
                mMRetriever.setDataSource(strings[0]);
            myBitmap = mMRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();


        } finally {
            if (mMRetriever != null) {
                mMRetriever.release();
            }
        }
        return myBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        ba.setVisibility(View.GONE);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        imageView.setVisibility(View.VISIBLE);

    }

}
