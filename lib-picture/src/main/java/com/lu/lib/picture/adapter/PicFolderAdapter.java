package com.lu.lib.picture.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.lib.picture.LPicture;
import com.lu.lib.picture.bean.PictureFolder;
import com.lu.lib.picture.util.DimensionTool;

import java.util.ArrayList;

import lu.photo.R;

/**
 * @Author: luqihua
 * @Time: 2018/6/12
 * @Description: 图片文件列表适配器
 */

public class PicFolderAdapter extends BaseRecyclerAdapter<PictureFolder> {

    public PicFolderAdapter(@NonNull Context context, ArrayList<PictureFolder> data) {
        super(context, data);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_picture_folder;
    }

    @Override
    protected void onCreateHolder(BaseViewHolder<PictureFolder> holder, int viewType) {
        ImageView ivPictureV = LPicture.getInstance()
                .getPicLoader()
                .createImageView(mContext);
        int size = DimensionTool.dp2px(80);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(size, size);
        ivPictureV.setId(R.id.iv_item_image);
        ((ViewGroup) holder.itemView).addView(ivPictureV, 0, params);
    }

    @Override
    protected void bindItemData(BaseViewHolder<PictureFolder> holder, PictureFolder itemData, int position) {
        ImageView ivPictureV = holder.getView(R.id.iv_item_image);
        TextView tvFolderNameV = holder.getView(R.id.tv_item_dir_name);
        TextView tvFolderCountV = holder.getView(R.id.tv_item_dir_count);


        ivPictureV.setImageResource(R.drawable.picture_no);
        tvFolderNameV.setText(itemData.getName());
        tvFolderCountV.setText(itemData.getCount() + "张");
        LPicture.getInstance()
                .getPicLoader()
                .displayImage(mContext, ivPictureV, itemData.getFirstImagePath());
    }
}
