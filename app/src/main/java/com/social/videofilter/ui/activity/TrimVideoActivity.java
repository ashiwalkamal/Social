package com.social.videofilter.ui.activity;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marvhong.videoeffect.FillMode;
import com.marvhong.videoeffect.GlVideoView;
import com.marvhong.videoeffect.composer.Mp4Composer;
import com.marvhong.videoeffect.helper.MagicFilterFactory;
import com.marvhong.videoeffect.helper.MagicFilterType;
import com.marvhong.videoeffect.utils.ConfigUtils;
import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.engine.TrackType;
import com.otaliastudios.transcoder.strategy.DefaultAudioStrategy;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy;
import com.social.Activity.Compressing;
import com.social.Activity.Final_step;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.Social;
import com.social.videofilter.adapter.TrimVideoAdapter;
import com.social.videofilter.base.BaseActivity;
import com.social.videofilter.helper.ToolbarHelper;
import com.social.videofilter.model.FilterModel;
import com.social.videofilter.model.VideoEditInfo;
import com.social.videofilter.utils.ExtractFrameWorkThread;
import com.social.videofilter.utils.ExtractVideoInfoUtil;
import com.social.videofilter.utils.UIUtils;
import com.social.videofilter.utils.VideoUtil;
import com.social.videofilter.view.NormalProgressDialog;
import com.social.videofilter.view.RangeSeekBar;
import com.social.videofilter.view.VideoThumbSpacingItemDecoration;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



/**
 * @author LLhon
 * @Project Android-Video-Editor
 * @Package com.marvhong.videoeditor
 * @Date 2018/8/21 17:51
 * @description 裁剪视频界面
 */
public class TrimVideoActivity extends BaseActivity {

    private static final String TAG = TrimVideoActivity.class.getSimpleName();
    private static final long MIN_CUT_DURATION = 3 * 1000L;// 最小剪辑时间3s
    private static final long MAX_CUT_DURATION = 15 * 1000L;//视频最多剪切多长时间
    private static final int MAX_COUNT_RANGE = 15;//seekBar的区域内一共有多少张图片
    private static final int MARGIN = UIUtils.dp2Px(0); //左右两边间距
    private final MainHandler mUIHandler = new MainHandler(this);
    @BindView(R.id.glsurfaceview)
    GlVideoView mSurfaceView;
    @BindView(R.id.video_shoot_tip)
    TextView mTvShootTip;
    @BindView(R.id.video_thumb_listview)
    RecyclerView mRecyclerView;
    @BindView(R.id.positionIcon)
    ImageView mIvPosition;
    @BindView(R.id.id_seekBarLayout)
    LinearLayout seekBarLayout;
    @BindView(R.id.layout_surface_view)
    RelativeLayout mRlVideo;
    @BindView(R.id.view_trim_indicator)
    View mViewTrimIndicator;
    @BindView(R.id.view_effect_indicator)
    View mViewEffectIndicator;
    @BindView(R.id.ll_trim_container)
    LinearLayout mLlTrimContainer;
    @BindView(R.id.hsv_effect)
    HorizontalScrollView mHsvEffect;
    @BindView(R.id.ll_effect_container)
    LinearLayout mLlEffectContainer;
    String videothumb = "";
    ProgressDialog dialog;
    private RangeSeekBar seekBar;
    private ExtractVideoInfoUtil mExtractVideoInfoUtil;
    private int mMaxWidth; //可裁剪区域的最大宽度
    private long duration; //视频总时长
    private TrimVideoAdapter videoEditAdapter;
    private float averageMsPx;//每毫秒所占的px
    private float averagePxMs;//每px所占用的ms毫秒
    private String OutPutFileDirPath;
    private ExtractFrameWorkThread mExtractFrameWorkThread;
    private long leftProgress, rightProgress; //裁剪视频左边区域的时间位置, 右边时间位置
    private long scrollPos = 0;
    private int mScaledTouchSlop;
    private int lastScrollX;
    private boolean isSeeking;
    private String mVideoPath;
    private int mOriginalWidth; //视频原始宽度
    private int mOriginalHeight; //视频原始高度
    private List<FilterModel> mVideoEffects = new ArrayList<>(); //视频滤镜效果
    private MagicFilterType[] mMagicFilterTypes;
    private ValueAnimator mEffectAnimator;
    private SurfaceTexture mSurfaceTexture;
    private MediaPlayer mMediaPlayer;
    private Mp4Composer mMp4Composer;
    private boolean isOverScaledTouchSlop;
    private ValueAnimator animator;
    private Handler handler = new Handler();
    private Runnable run = new Runnable() {

        @Override
        public void run() {
            videoProgressUpdate();
            handler.postDelayed(run, 1000);
        }
    };
    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.d(TAG, "-------newState:>>>>>" + newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isSeeking = false;
//                videoStart();
            } else {
                isSeeking = true;
                if (isOverScaledTouchSlop) {
                    videoPause();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            isSeeking = false;
            int scrollX = getScrollXDistance();
            //达不到滑动的距离
            if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
                isOverScaledTouchSlop = false;
                return;
            }
            isOverScaledTouchSlop = true;
            Log.d(TAG, "-------scrollX:>>>>>" + scrollX);
            //初始状态,why ? 因为默认的时候有56dp的空白！
            if (scrollX == -MARGIN) {
                scrollPos = 0;
            } else {
                // why 在这里处理一下,因为onScrollStateChanged早于onScrolled回调
                videoPause();
                isSeeking = true;
                scrollPos = (long) (averageMsPx * (MARGIN + scrollX));
                Log.d(TAG, "-------scrollPos:>>>>>" + scrollPos);
                leftProgress = seekBar.getSelectedMinValue() + scrollPos;
                rightProgress = seekBar.getSelectedMaxValue() + scrollPos;
                Log.d(TAG, "-------leftProgress:>>>>>" + leftProgress);
                mMediaPlayer.seekTo((int) leftProgress);
            }
            lastScrollX = scrollX;
        }
    };
    private final RangeSeekBar.OnRangeSeekBarChangeListener mOnRangeSeekBarChangeListener = new RangeSeekBar.OnRangeSeekBarChangeListener() {
        @Override
        public void onRangeSeekBarValuesChanged(RangeSeekBar bar, long minValue, long maxValue,
                                                int action, boolean isMin, RangeSeekBar.Thumb pressedThumb) {
            Log.d(TAG, "-----minValue----->>>>>>" + minValue);
            Log.d(TAG, "-----maxValue----->>>>>>" + maxValue);
            leftProgress = minValue + scrollPos;
            rightProgress = maxValue + scrollPos;
            Log.d(TAG, "-----leftProgress----->>>>>>" + leftProgress);
            Log.d(TAG, "-----rightProgress----->>>>>>" + rightProgress);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "-----ACTION_DOWN---->>>>>>");
                    isSeeking = false;
                    videoPause();
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "-----ACTION_MOVE---->>>>>>");
                    isSeeking = true;
                    mMediaPlayer.seekTo((int) (pressedThumb == RangeSeekBar.Thumb.MIN ?
                            leftProgress : rightProgress));
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "-----ACTION_UP--leftProgress--->>>>>>" + leftProgress);
                    isSeeking = false;
                    //从minValue开始播
                    mMediaPlayer.seekTo((int) leftProgress);
//                    videoStart();
                    mTvShootTip
                            .setText(String.format("Crop %d s", (rightProgress - leftProgress) / 1000));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trim_video;
    }

    @Override
    protected void init() {
        mVideoPath = getIntent().getStringExtra("videoPath");
        videothumb = getIntent().getStringExtra("videothumb");
        mExtractVideoInfoUtil = new ExtractVideoInfoUtil(mVideoPath);
        mMaxWidth = UIUtils.getScreenWidth() - MARGIN * 2;

        mScaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) {
                e.onNext(mExtractVideoInfoUtil.getVideoLength());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(String s) {
                        duration = Long.valueOf(mExtractVideoInfoUtil.getVideoLength());
                        //矫正获取到的视频时长不是整数问题
                        float tempDuration = duration / 1000f;
                        duration = new BigDecimal(tempDuration).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() * 1000;
                        Log.e(TAG, "视频总时长：" + duration);
                        initEditVideo();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void initToolbar(ToolbarHelper toolbarHelper) {
        toolbarHelper.setTitle("Crop");
        toolbarHelper.setMenuTitle("Release", v -> {
            trimmerVideo();
        });
    }

    @Override
    protected void initView() {
        mRecyclerView
                .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        videoEditAdapter = new TrimVideoAdapter(this, mMaxWidth / 10);
        mRecyclerView.setAdapter(videoEditAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        mSurfaceView.init(surfaceTexture -> {
            mSurfaceTexture = surfaceTexture;
            initMediaPlayer(surfaceTexture);
        });

        //滤镜效果集合
        mMagicFilterTypes = new MagicFilterType[]{
                MagicFilterType.NONE, MagicFilterType.INVERT,
                MagicFilterType.SEPIA, MagicFilterType.BLACKANDWHITE,
                MagicFilterType.TEMPERATURE, MagicFilterType.OVERLAY,
                MagicFilterType.BARRELBLUR, MagicFilterType.POSTERIZE,
                MagicFilterType.CONTRAST, MagicFilterType.GAMMA,
                MagicFilterType.HUE, MagicFilterType.CROSSPROCESS,
                MagicFilterType.GRAYSCALE, MagicFilterType.CGACOLORSPACE,
        };

        for (int i = 0; i < mMagicFilterTypes.length; i++) {
            FilterModel model = new FilterModel();
            model.setName(
                    UIUtils.getString(MagicFilterFactory.filterType2Name(mMagicFilterTypes[i])));
            mVideoEffects.add(model);
        }

        addEffectView();
    }

    @OnClick({R.id.ll_trim_tab, R.id.ll_effect_tab})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_trim_tab: //裁切tab
                mViewTrimIndicator.setVisibility(View.VISIBLE);
                mViewEffectIndicator.setVisibility(View.GONE);
                mLlTrimContainer.setVisibility(View.VISIBLE);
                mHsvEffect.setVisibility(View.GONE);
                break;
            case R.id.ll_effect_tab: //滤镜tab
                mViewTrimIndicator.setVisibility(View.GONE);
                mViewEffectIndicator.setVisibility(View.VISIBLE);
                mLlTrimContainer.setVisibility(View.GONE);
                mHsvEffect.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 动态添加滤镜效果View
     */
    private void addEffectView() {
        mLlEffectContainer.removeAllViews();
        for (int i = 0; i < mVideoEffects.size(); i++) {
            View itemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_video_effect, mLlEffectContainer, false);
            TextView tv = itemView.findViewById(R.id.tv);
            ImageView iv = itemView.findViewById(R.id.iv);
            FilterModel model = mVideoEffects.get(i);
            int thumbId = MagicFilterFactory.filterType2Thumb(mMagicFilterTypes[i]);
            Glide.with(Social.sApplication)
                    .load(thumbId)
                    .into(iv);
            tv.setText(model.getName());
            int index = i;
            itemView.setOnClickListener(v -> {
                for (int j = 0; j < mLlEffectContainer.getChildCount(); j++) {
                    View tempItemView = mLlEffectContainer.getChildAt(j);
                    TextView tempTv = tempItemView.findViewById(R.id.tv);
                    FilterModel tempModel = mVideoEffects.get(j);
                    if (j == index) {
                        //选中的滤镜效果
                        if (!tempModel.isChecked()) {
                            openEffectAnimation(tempTv, tempModel, true);
                        }
                        ConfigUtils.getInstance().setMagicFilterType(mMagicFilterTypes[j]);
                        mSurfaceView.setFilter(MagicFilterFactory.getFilter());
                    } else {
                        //未选中的滤镜效果
                        if (tempModel.isChecked()) {
                            openEffectAnimation(tempTv, tempModel, false);
                        }
                    }
                }
            });
            mLlEffectContainer.addView(itemView);
        }
    }

    private void openEffectAnimation(TextView tv, FilterModel model, boolean isExpand) {
        model.setChecked(isExpand);
        int startValue = UIUtils.dp2Px(30);
        int endValue = UIUtils.dp2Px(100);
        if (!isExpand) {
            startValue = UIUtils.dp2Px(100);
            endValue = UIUtils.dp2Px(30);
        }
        mEffectAnimator = ValueAnimator.ofInt(startValue, endValue);
        mEffectAnimator.setDuration(300);
        mEffectAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT, value, Gravity.BOTTOM);
                tv.setLayoutParams(params);
                tv.requestLayout();
            }
        });
        mEffectAnimator.start();
    }

    private void initEditVideo() {
        //for video edit
        long startPosition = 0;
        long endPosition = duration;
        int thumbnailsCount;
        int rangeWidth;
        boolean isOver_10_s;
        if (endPosition <= MAX_CUT_DURATION) {
            isOver_10_s = false;
            thumbnailsCount = MAX_COUNT_RANGE;
            rangeWidth = mMaxWidth;
        } else {
            isOver_10_s = true;
            thumbnailsCount = (int) (endPosition * 1.0f / (MAX_CUT_DURATION * 1.0f)
                    * MAX_COUNT_RANGE);
            rangeWidth = mMaxWidth / MAX_COUNT_RANGE * thumbnailsCount;
        }
        mRecyclerView.addItemDecoration(new VideoThumbSpacingItemDecoration(MARGIN, thumbnailsCount));

        //init seekBar
        if (isOver_10_s) {
            seekBar = new RangeSeekBar(this, 0L, MAX_CUT_DURATION);
            seekBar.setSelectedMinValue(0L);
            seekBar.setSelectedMaxValue(MAX_CUT_DURATION);
        } else {
            seekBar = new RangeSeekBar(this, 0L, endPosition);
            seekBar.setSelectedMinValue(0L);
            seekBar.setSelectedMaxValue(endPosition);
        }
        seekBar.setMin_cut_time(MIN_CUT_DURATION);//设置最小裁剪时间
        seekBar.setNotifyWhileDragging(true);
        seekBar.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener);
        seekBarLayout.addView(seekBar);

        Log.d(TAG, "-------thumbnailsCount--->>>>" + thumbnailsCount);
        averageMsPx = duration * 1.0f / rangeWidth * 1.0f;
        Log.d(TAG, "-------rangeWidth--->>>>" + rangeWidth);
        Log.d(TAG, "-------localMedia.getDuration()--->>>>" + duration);
        Log.d(TAG, "-------averageMsPx--->>>>" + averageMsPx);
        OutPutFileDirPath = VideoUtil.getSaveEditThumbnailDir(this);
        int extractW = mMaxWidth / MAX_COUNT_RANGE;
        int extractH = UIUtils.dp2Px(62);
        mExtractFrameWorkThread = new ExtractFrameWorkThread(extractW, extractH, mUIHandler,
                mVideoPath,
                OutPutFileDirPath, startPosition, endPosition, thumbnailsCount);
        mExtractFrameWorkThread.start();

        //init pos icon start
        leftProgress = 0;
        if (isOver_10_s) {
            rightProgress = MAX_CUT_DURATION;
        } else {
            rightProgress = endPosition;
        }
        mTvShootTip.setText(String.format("Crop %d s", rightProgress / 1000));
        averagePxMs = (mMaxWidth * 1.0f / (rightProgress - leftProgress));
        Log.d(TAG, "------averagePxMs----:>>>>>" + averagePxMs);
    }

    /**
     * 初始化MediaPlayer
     */
    private void initMediaPlayer(SurfaceTexture surfaceTexture) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mVideoPath);
            Surface surface = new Surface(surfaceTexture);
            mMediaPlayer.setSurface(surface);
            surface.release();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();
                    float videoProportion = (float) videoWidth / (float) videoHeight;
                    int screenWidth = mRlVideo.getWidth();
                    int screenHeight = mRlVideo.getHeight();
                    float screenProportion = (float) screenWidth / (float) screenHeight;
                    if (videoProportion > screenProportion) {
                        lp.width = screenWidth;
                        lp.height = (int) ((float) screenWidth / videoProportion);
                    } else {
                        lp.width = (int) (videoProportion * (float) screenHeight);
                        lp.height = screenHeight;
                    }
                    mSurfaceView.setLayoutParams(lp);

                    mOriginalWidth = videoWidth;
                    mOriginalHeight = videoHeight;
                    Log.e("videoView", "videoWidth:" + videoWidth + ", videoHeight:" + videoHeight);

                    //设置MediaPlayer的OnSeekComplete监听
                    mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            Log.d(TAG, "------ok----real---start-----");
                            Log.d(TAG, "------isSeeking-----" + isSeeking);
                            if (!isSeeking) {
                                videoStart();
                            }
                        }
                    });
                }
            });
            mMediaPlayer.prepare();
            videoStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 视频裁剪
     */
    private void trimmerVideo() {

        Utlity.show_progress(this);
        videoPause();
        Log.e(TAG, "trimVideo...startSecond:" + leftProgress + ", endSecond:"
                + rightProgress); //start:44228, end:48217
        final String compress = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+System.currentTimeMillis()+".mp4";

        VideoUtil.cutVideo(mVideoPath, compress, leftProgress / 1000, rightProgress / 1000)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(String outputPath) {
                        // /storage/emulated/0/Android/data/com.kangoo.diaoyur/files/small_video/trimmedVideo_20180416_153217.mp4
                        Log.e(TAG, "cutVideo---onSuccess");
                        try {
                            startMediaCodec(outputPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.e(TAG, "cutVideo---onError:" + e.toString());
                        Utlity.dismiss_dilog(TrimVideoActivity.this);
                        Toast.makeText(TrimVideoActivity.this, "视频裁剪失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 视频添加滤镜效果
     */
    private void startMediaCodec(String srcPath) {
        String compress = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+System.currentTimeMillis()+".mp4";
        mMp4Composer = new Mp4Composer(srcPath, compress)
                // .rotation(Rotation.ROTATION_270)
                 .size(mOriginalWidth, mOriginalHeight)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .filter(MagicFilterFactory.getFilter())
                .mute(false)
                .flipHorizontal(false)
                .flipVertical(false)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                    }

                    @Override
                    public void onCompleted() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utlity.dismiss_dilog(TrimVideoActivity.this);
                                Intent intent = new Intent(TrimVideoActivity.this, Compressing.class);
                                intent.putExtra("path", compress);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCanceled() {
                        NormalProgressDialog.stopLoading();
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        Utlity.dismiss_dilog(TrimVideoActivity.this);
                    }
                })
                .start();
    }

    /**
     * 视频压缩
     */
    private void compressVideo(final String srcPath) {

    }
    /**
     * 水平滑动了多少px
     *
     * @return int px
     */
    private int getScrollXDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int itemWidth = firstVisibleChildView.getWidth();
        return (position) * itemWidth - firstVisibleChildView.getLeft();
    }

    private void anim() {
        Log.d(TAG, "--anim--onProgressUpdate---->>>>>>>" + mMediaPlayer.getCurrentPosition());
        if (mIvPosition.getVisibility() == View.GONE) {
            mIvPosition.setVisibility(View.VISIBLE);
        }
        final LayoutParams params = (LayoutParams) mIvPosition
                .getLayoutParams();
        int start = (int) (MARGIN
                + (leftProgress/*mVideoView.getCurrentPosition()*/ - scrollPos) * averagePxMs);
        int end = (int) (MARGIN + (rightProgress - scrollPos) * averagePxMs);
        animator = ValueAnimator
                .ofInt(start, end)
                .setDuration(
                        (rightProgress - scrollPos) - (leftProgress/*mVideoView.getCurrentPosition()*/
                                - scrollPos));
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.leftMargin = (int) animation.getAnimatedValue();
                mIvPosition.setLayoutParams(params);
            }
        });
        animator.start();
    }

    private void videoStart() {
        Log.d(TAG, "----videoStart----->>>>>>>");
        mMediaPlayer.start();
        mIvPosition.clearAnimation();
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        anim();
        handler.removeCallbacks(run);
        handler.post(run);
    }

    private void videoProgressUpdate() {
        long currentPosition = mMediaPlayer.getCurrentPosition();
        Log.d(TAG, "----onProgressUpdate-cp---->>>>>>>" + currentPosition);
        if (currentPosition >= (rightProgress)) {
            mMediaPlayer.seekTo((int) leftProgress);
            mIvPosition.clearAnimation();
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
            anim();
        }
    }

    private void videoPause() {
        isSeeking = false;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            handler.removeCallbacks(run);
        }
        Log.d(TAG, "----videoPause----->>>>>>>");
        if (mIvPosition.getVisibility() == View.VISIBLE) {
            mIvPosition.setVisibility(View.GONE);
        }
        mIvPosition.clearAnimation();
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo((int) leftProgress);
//            videoStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPause();
    }

    @Override
    protected void onDestroy() {
        NormalProgressDialog.stopLoading();
        ConfigUtils.getInstance().setMagicFilterType(MagicFilterType.NONE);
        if (animator != null) {
            animator.cancel();
        }
        if (mEffectAnimator != null) {
            mEffectAnimator.cancel();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mMp4Composer != null) {
            mMp4Composer.cancel();
        }
        if (mExtractVideoInfoUtil != null) {
            mExtractVideoInfoUtil.release();
        }
        if (mExtractFrameWorkThread != null) {
            mExtractFrameWorkThread.stopExtract();
        }
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mUIHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        //删除视频每一帧的预览图
        if (!TextUtils.isEmpty(OutPutFileDirPath)) {
            VideoUtil.deleteFile(new File(OutPutFileDirPath));
        }
        //删除裁剪后的视频，滤镜视频
        String trimmedDirPath = VideoUtil.getTrimmedVideoDir(this, "small_video/trimmedVideo");
        if (!TextUtils.isEmpty(trimmedDirPath)) {
            VideoUtil.deleteFile(new File(trimmedDirPath));
        }
        super.onDestroy();
    }

    private static class MainHandler extends Handler {

        private final WeakReference<TrimVideoActivity> mActivity;

        MainHandler(TrimVideoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TrimVideoActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == ExtractFrameWorkThread.MSG_SAVE_SUCCESS) {
                    if (activity.videoEditAdapter != null) {
                        VideoEditInfo info = (VideoEditInfo) msg.obj;
                        activity.videoEditAdapter.addItemVideoInfo(info);
                    }
                }
            }
        }
    }
}
