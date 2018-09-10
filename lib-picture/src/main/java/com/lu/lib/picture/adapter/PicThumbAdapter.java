package com.lu.lib.picture.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lu.lib.picture.LPicture;
import com.lu.lib.picture.bean.PictureItem;
import com.lu.lib.picture.util.DimensionTool;

import java.util.ArrayList;
import java.util.List;

import lu.photo.R;

/**
 * @author luqihua
 * @time 2018/6/12
 * @description 缩略图适配器
 */

public class PicThumbAdapter extends BaseRecyclerAdapter<PictureItem> {

    private int mSelectPosition;

    public PicThumbAdapter(Context context, ArrayList<PictureItem> data) {
        super(context, data);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_picture_thumb;
    }

    @Override
    protected void onCreateHolder(BaseViewHolder<PictureItem> holder, int viewType) {
        ImageView ivPictureV = LPicture.getInstance()
                .getPicLoader()
                .createImageView(mContext);
        int size = DimensionTool.dp2px(60);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(size, size);
        ivPictureV.setId(R.id.iv_item_image);
        ((ViewGroup) holder.itemView).addView(ivPictureV, 0, params);
    }

    @Override
    protected void bindItemData(BaseViewHolder<PictureItem> holder, PictureItem itemData, int position) {
        final ImageView ivPictureV = holder.getView(R.id.iv_item_image);
        final View vFrameV = holder.getView(R.id.selected_frame);

        LPicture.getInstance()
                .getPicLoader()
                .displayImage(mContext, ivPictureV, itemData.getPath());

        if (mSelectPosition == position) {
            vFrameV.setVisibility(View.VISIBLE);
        } else {
            vFrameV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.size() > 0) {
            final View vFrameV = ((BaseViewHolder) holder).getView(R.id.selected_frame);
            if (mSelectPosition == position) {
                vFrameV.setVisibility(View.VISIBLE);
            } else {
                vFrameV.setVisibility(View.GONE);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }


    public void setCurrentItem(PictureItem holder) {
        mSelectPosition = mData.indexOf(holder);
        notifyItemRangeChanged(0, mData.size(), "refresh_frame");
    }


    public void addData(PictureItem item) {
        if (mData.contains(item)) return;
        mData.add(item);
        mSelectPosition = mData.indexOf(item);
        notifyItemChanged(mData.size() - 1);
    }

    public void removeData(PictureItem item) {
        if (!mData.contains(item)) return;
        int position = mData.indexOf(item);
        mData.remove(position);
        mSelectPosition = -1;
        notifyItemRemoved(position);
    }

    public ArrayList<PictureItem> getData() {
        return mData;
    }

    public boolean containData(PictureItem item) {
        return mData.contains(item);
    }

}
