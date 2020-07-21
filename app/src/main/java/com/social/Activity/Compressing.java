package com.social.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.TranscoderOptions;
import com.otaliastudios.transcoder.engine.TrackStatus;
import com.otaliastudios.transcoder.engine.TrackType;
import com.otaliastudios.transcoder.sink.DataSink;
import com.otaliastudios.transcoder.sink.DefaultDataSink;
import com.otaliastudios.transcoder.source.DataSource;
import com.otaliastudios.transcoder.source.TrimDataSource;
import com.otaliastudios.transcoder.source.UriDataSource;
import com.otaliastudios.transcoder.strategy.DefaultAudioStrategy;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy;
import com.otaliastudios.transcoder.strategy.RemoveTrackStrategy;
import com.otaliastudios.transcoder.strategy.TrackStrategy;
import com.otaliastudios.transcoder.strategy.size.AspectRatioResizer;
import com.otaliastudios.transcoder.strategy.size.AtMostResizer;
import com.otaliastudios.transcoder.strategy.size.FractionResizer;
import com.otaliastudios.transcoder.strategy.size.PassThroughResizer;
import com.otaliastudios.transcoder.validator.DefaultValidator;
import com.social.Helpers.FileUtils;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ActivityCompressingBinding;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

public class Compressing extends AppCompatActivity implements
        TranscoderListener {
    private static final int REQUEST_CODE_PICK = 1;
    private static final int REQUEST_CODE_PICK_AUDIO = 5;
    private static final int PROGRESS_BAR_MAX = 1000;
    ActivityCompressingBinding binding;
    String video_path = "";
    int sampleRate, channels, frames, rotation;
    boolean removeAudio = false;
    Float fraction, aspectRatio, speed;
    private boolean mIsTranscoding;
    private boolean mIsAudioOnly;
    private Future<Void> mTranscodeFuture;
    private Uri mTranscodeInputUri1;
    private Uri mTranscodeInputUri2;
    private Uri mTranscodeInputUri3;
    private Uri mAudioReplacementUri;
    private File mTranscodeOutputFile;
    private long mTranscodeStartTime;
    private TrackStrategy mTranscodeVideoStrategy;
    private TrackStrategy mTranscodeAudioStrategy;
    private long mTrimStartUs = 0;
    private long mTrimEndUs = 0;
    private boolean is_audio = false;
    private String final_video="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compressing);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        final_video= Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+System.currentTimeMillis()+".mp4";
        //for compressing video
        sampleRate = DefaultAudioStrategy.SAMPLE_RATE_AS_INPUT;
        channels = DefaultAudioStrategy.CHANNELS_AS_INPUT;
        removeAudio = false;
        frames = DefaultVideoStrategy.DEFAULT_FRAME_RATE;
        fraction = 1F;
        aspectRatio = 16F / 9F;
        speed = 1F;
        rotation = 0;
        if (removeAudio) {
            mTranscodeAudioStrategy = new RemoveTrackStrategy();
        } else {
            mTranscodeAudioStrategy = DefaultAudioStrategy.builder()
                    .channels(channels)
                    .sampleRate(sampleRate)
                    .build();
        }
//        mTranscodeVideoStrategy = new DefaultVideoStrategy.Builder()
//                .addResizer(aspectRatio > 0 ? new AspectRatioResizer(aspectRatio) : new PassThroughResizer())
//                .addResizer(new FractionResizer(fraction))
//                .frameRate(frames)
//                .build();
        mTranscodeVideoStrategy = new DefaultVideoStrategy.Builder()
                .build();

        if (mTrimStartUs < 0) mTrimStartUs = 0;
        if (mTrimEndUs < 0) mTrimEndUs = 0;

        //set
        if (getIntent() != null) {
            video_path = getIntent().getStringExtra("path");
            video_inti(video_path);
            Uri data = Uri.parse(video_path);
            if (data != null) {
                mTranscodeInputUri1 = data;
                mTranscodeInputUri2 = null;
                mTranscodeInputUri3 = null;
            }
        }


    }

    private void setIsTranscoding(boolean isTranscoding) {
        mIsTranscoding = isTranscoding;
    }


    private void video_inti(String video_path) {
        Bitmap bitmap = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ThumbnailUtils.createVideoThumbnail(video_path, MediaStore.Video.Thumbnails.MINI_KIND);
            } else {
                bitmap = ThumbnailUtils.createVideoThumbnail(FileUtils.getPath(Compressing.this, Uri.parse(video_path)), MediaStore.Video.Thumbnails.MINI_KIND);
            }
            binding.thumb.setImageBitmap(bitmap);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            binding.thumb.setImageDrawable(this.getResources().getDrawable(R.drawable.empty));

        }

        binding.video.setVideoPath(video_path);

        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.thumb.setVisibility(View.GONE);
                binding.video.setVisibility(View.VISIBLE);
                binding.play.setVisibility(View.GONE);
                binding.video.start();
            }
        });

        binding.video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.thumb.setVisibility(View.VISIBLE);
                binding.video.setVisibility(View.GONE);
                binding.play.setVisibility(View.VISIBLE);
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_audio) {
                    is_audio = false;
                    Intent intent = new Intent(Compressing.this, Final_step.class);
                    intent.putExtra("path", mTranscodeOutputFile.getAbsolutePath());
                    startActivity(intent);
                    finish();


                } else {
                    transcode();
                }
            }
        });

        binding.llaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioReplacementUri = null;
                if (!mIsTranscoding) {
                    startActivityForResult(new Intent(Compressing.this,Sound_selector.class), REQUEST_CODE_PICK_AUDIO);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_AUDIO
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            mAudioReplacementUri = data.getData();
            binding.songselected.setText(mAudioReplacementUri.getLastPathSegment());
            is_audio = true;
            transcode();
        }
    }


    private void transcode() {
        Utlity.show_progress(this);
        // Create a temporary file for output.
        try {
            File outputDir = new File(getExternalFilesDir(null), "outputs");
            //noinspection ResultOfMethodCallIgnored
            outputDir.mkdir();
            mTranscodeOutputFile = File.createTempFile("transcode_test", ".mp4", outputDir);

        } catch (IOException e) {
            Toast.makeText(this, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
            return;
        }
        // Launch the transcoding operation.
        mTranscodeStartTime = SystemClock.uptimeMillis();
        setIsTranscoding(true);
        DataSink sink = new DefaultDataSink(mTranscodeOutputFile.getAbsolutePath());
        TranscoderOptions.Builder builder = Transcoder.into(sink);
        if (mAudioReplacementUri == null) {
            if (mTranscodeInputUri1 != null) {
                DataSource source = new UriDataSource(this, mTranscodeInputUri1);
                builder.addDataSource(new TrimDataSource(source, mTrimStartUs, mTrimEndUs));
            }
            if (mTranscodeInputUri2 != null) builder.addDataSource(this, mTranscodeInputUri2);
            if (mTranscodeInputUri3 != null) builder.addDataSource(this, mTranscodeInputUri3);
        } else {
            if (mTranscodeInputUri1 != null) {
                DataSource source = new UriDataSource(this, mTranscodeInputUri1);
                builder.addDataSource(TrackType.VIDEO, new TrimDataSource(source, mTrimStartUs, mTrimEndUs));
            }
            if (mTranscodeInputUri2 != null)
                builder.addDataSource(TrackType.VIDEO, this, mTranscodeInputUri2);
            if (mTranscodeInputUri3 != null)
                builder.addDataSource(TrackType.VIDEO, this, mTranscodeInputUri3);
            builder.addDataSource(TrackType.AUDIO, this, mAudioReplacementUri);
        }
        mTranscodeFuture = builder.setListener(this)
                .setAudioTrackStrategy(mTranscodeAudioStrategy)
                .setVideoTrackStrategy(mTranscodeVideoStrategy)
                .setVideoRotation(rotation)
                .setValidator(new DefaultValidator() {
                    @Override
                    public boolean validate(@NonNull TrackStatus videoStatus, @NonNull TrackStatus audioStatus) {
                        mIsAudioOnly = !videoStatus.isTranscoding();
                        return super.validate(videoStatus, audioStatus);
                    }
                })
                .setSpeed(speed)
                .transcode();
    }

    @Override
    public void onTranscodeProgress(double progress) {

    }

    @Override
    public void onTranscodeCompleted(int successCode) {
        if (successCode == Transcoder.SUCCESS_TRANSCODED) {
            Utlity.dismiss_dilog(this);
            onTranscodeFinished(true, "Transcoded file placed on " + mTranscodeOutputFile);
            if (is_audio) {
                binding.video.setVideoPath(mTranscodeOutputFile.getPath());
                binding.thumb.setVisibility(View.GONE);
                binding.video.setVisibility(View.VISIBLE);
                binding.play.setVisibility(View.GONE);
                binding.video.start();
            } else {
                is_audio = false;
                Intent intent = new Intent(Compressing.this, Final_step.class);
                intent.putExtra("path", mTranscodeOutputFile.getAbsolutePath());
                startActivity(intent);
                finish();

            }
        } else if (successCode == Transcoder.SUCCESS_NOT_NEEDED) {
            onTranscodeFinished(true, "Transcoding not needed, source file untouched.");
        }
    }

    @Override
    public void onTranscodeCanceled() {
        Utlity.dismiss_dilog(this);
        onTranscodeFinished(false, "Transcoder canceled.");
    }

    @Override
    public void onTranscodeFailed(@NonNull Throwable exception) {
        Utlity.dismiss_dilog(this);
        onTranscodeFinished(false, "Transcoder error occurred. " + exception.getMessage());
    }

    private void onTranscodeFinished(boolean isSuccess, String toastMessage) {
        Utlity.dismiss_dilog(this);
        setIsTranscoding(false);
        long max = 10 * (1024 * 1024);
        // Get the number of bytes in the file
        long sizeInBytes = mTranscodeOutputFile.length();
        //transform in MB
        long sizeInMb = sizeInBytes / (1024 * 1024);
        Log.d("file_size>>", String.valueOf(sizeInMb)+" MB");
        if (sizeInMb < max) {
            Utlity.show_toast(this, "Video ready for publish !");
        } else {
            Utlity.show_toast(this, "Please wait a moment video will ready for publish !");

        }
    }

}