package com.social.videofilter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.social.R;
import com.social.videofilter.model.LocalVideoModel;
import com.social.videofilter.utils.UIUtils;
import com.social.videofilter.utils.VideoUtil;

import java.util.List;

/**
 * @author LLhon
 * @Project diaoyur_android
 * @Package com.marvhong.videoeditor
 * @Date 2018/8/21 15:16
 * @description
 */
public class VideoGridAdapter extends RecyclerView.Adapter<VideoGridAdapter.VideoHolder> {

    private Context mContext;
    private List<LocalVideoModel> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public VideoGridAdapter(Context context, List<LocalVideoModel> data) {
        mContext = context;
        mDatas = data;
    }

    public void setData(List<LocalVideoModel> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_grid_video, null, false));
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {
        final LocalVideoModel model = mDatas.get(position);
        Glide.with(mContext)
            .load(VideoUtil.getVideoFilePath(model.getVideoPath()))
            .into(holder.mIv);

        holder.mTvDuration.setText(VideoUtil.convertSecondsToTime(model.getDuration() / 1000));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, model);
                }
            }
        });
    }

    class VideoHolder extends RecyclerView.ViewHolder {

        ImageView mIv;
        TextView mTvDuration;

        public VideoHolder(View itemView) {
            super(itemView);
            mIv = itemView.findViewById(R.id.iv);
            mTvDuration = itemView.findViewById(R.id.tv_duration);
            int size = UIUtils.getScreenWidth() / 4;
            LayoutParams params = (LayoutParams) mIv.getLayoutParams();
            params.width = size;
            params.height = size;
            mIv.setLayoutParams(params);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LocalVideoModel model);
    }
}
