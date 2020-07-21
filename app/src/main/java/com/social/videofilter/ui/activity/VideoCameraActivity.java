package com.social.videofilter.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.social.R;
import com.social.videofilter.base.BaseActivity;

import java.io.File;

import butterknife.BindView;

/**
 * @author LLhon
 * @Project Android-Video-Editor
 * @Package com.marvhong.videoeditor
 * @Date 2018/8/22 10:54
 * @description 视频拍摄界面
 */
public class VideoCameraActivity extends BaseActivity {

//    @BindView(R.id.jcameraview)
//    JCameraView mJCameraView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_camera;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void initView() {
        //设置视频保存路径
//        mJCameraView.setSaveVideoPath(
//            Environment.getExternalStorageDirectory().getPath() + File.separator
//                + "videoeditor" + File.separator + "small_video");
//        mJCameraView.setMinDuration(5000); //设置最短录制时长
//        mJCameraView.setDuration(10000); //设置最长录制时长
//        mJCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
//        mJCameraView.setTip("Long press to shoot, 5~10second");
//        mJCameraView.setRecordShortTip("Recording time5~10second");
//        mJCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
//        mJCameraView.setErrorLisenter(new ErrorListener() {
//            @Override
//            public void onError() {
//                //错误监听
//                Log.d("CJT", "camera error");
//                Intent intent = new Intent();
//                setResult(103, intent);
//                finish();
//            }
//
//            @Override
//            public void AudioPermissionError() {
//                Toast.makeText(VideoCameraActivity.this, "Is it possible to give some recording rights?", Toast.LENGTH_SHORT).show();
//            }
//        });
//        //JCameraView监听
//        mJCameraView.setJCameraLisenter(new JCameraListener() {
//            @Override
//            public void captureSuccess(Bitmap bitmap) {
//                //获取图片bitmap
////                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
//                String path = FileUtil.saveBitmap("small_video", bitmap);
//            }
//
//            @Override
//            public void recordSuccess(String url, Bitmap firstFrame) {
//                //获取视频路径
//                String path = FileUtil.saveBitmap("small_video", firstFrame);
//                //url:/storage/emulated/0/haodiaoyu/small_video/video_1508930416375.mp4, Bitmap:/storage/emulated/0/haodiaoyu/small_video/picture_1508930429832.jpg
//                Log.d("CJT", "url:" + url + ", firstFrame:" + path);
//
//                Intent intent = new Intent(VideoCameraActivity.this, TrimVideoActivity.class);
//                intent.putExtra("videoPath", url);
//                intent.putExtra("videothumb", path);
//                startActivity(intent);
//                finish();
//            }
//        });
//        mJCameraView.setLeftClickListener(new ClickListener() {
//            @Override
//            public void onClick() {
//                finish();
//            }
//        });
//        mJCameraView.setRightClickListener(new ClickListener() {
//            @Override
//            public void onClick() {
//                Toast.makeText(VideoCameraActivity.this, "Right", Toast.LENGTH_SHORT).show();
//            }
//        });
//        mJCameraView.setRecordStateListener(new RecordStateListener() {
//            @Override
//            public void recordStart() {
//
//            }
//
//            @Override
//            public void recordEnd(long time) {
//                Log.e("Recording", "Recording time：" + time);
//            }
//
//            @Override
//            public void recordCancel() {
//
//            }
//        });
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mJCameraView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mJCameraView.onPause();
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
}
