package com.lu.lib.picture.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lu.lib.picture.LPicture;
import com.lu.lib.picture.bean.PictureItem;
import com.lu.lib.picture.util.DimensionTool;

import java.util.ArrayList;
import java.util.Collection;

import lu.photo.R;

/**
 * @author luqihua
 * @time 2018/6/12
 * @description 相册图片适配器
 */
public class PictureListAdapter extends BaseRecyclerAdapter<PictureItem> {

    private int mMaxCount;
    private int mImageWidth;//图片宽度

    private ArrayList<PictureItem> mSelectImages = new ArrayList<>();

    private OnItemChangeListener mOnItemSelectedListener;

    public PictureListAdapter(Context context, int maxCount) {
        super(context);
        this.mMaxCount = maxCount;
        mImageWidth = getImageWidth();
    }

    private int getImageWidth() {
        //图片尺寸取屏幕的1/3，一行最多三张
        return DimensionTool.getScreenWidth() / 3;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_picture_item;
    }

    @Override
    protected void onCreateHolder(final BaseViewHolder<PictureItem> holder, int viewType) {
        ImageView ivPictureV = LPicture.getInstance().getPicLoader().createImageView(mContext);
        ivPictureV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ivPictureV.setMaxWidth(mImageWidth);
        ivPictureV.setId(R.id.iv_item_image);
        ((ViewGroup) holder.itemView).addView(ivPictureV, 0, params);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.itemClick(mSelectImages, mData.get(position), position);
                }
            }
        });

        final AppCompatCheckedTextView btnSelectV = holder.getView(R.id.item_select);
        btnSelectV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = (int) v.getTag();
                final PictureItem itemData = mData.get(position);
                //已经被选中
                if (mSelectImages.contains(itemData)) {
                    mSelectImages.remove(itemData);
                } else if (mSelectImages.size() < mMaxCount) {
                    mSelectImages.add(itemData);
                }
                notifyItemChanged(position);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.itemSelect(mSelectImages);
                }
            }
        });

    }

    @Override
    protected void bindItemData(BaseViewHolder<PictureItem> holder, PictureItem itemData, final int position) {
        final PictureItem imageHolder = mData.get(position);
        final AppCompatCheckedTextView btnSelectV = holder.getView(R.id.item_select);
        final ImageView ivImageV = holder.getView(R.id.iv_item_image);

        ivImageV.setColorFilter(null);

        if (mSelectImages.contains(imageHolder)) {
            ivImageV.setColorFilter(Color.parseColor("#77000000"));
            btnSelectV.setChecked(true);
        } else {
            ivImageV.setColorFilter(null);
            btnSelectV.setChecked(false);
        }

        LPicture.getInstance()
                .getPicLoader()
                .displayImage(mContext, ivImageV, imageHolder.getPath());

        /*绑定数据给view*/
        btnSelectV.setTag(position);
        holder.itemView.setTag(position);
    }

    /**
     * 获取被选中的图片结合
     *
     * @return
     */
    public ArrayList<PictureItem> getSelectImages() {
        return mSelectImages;
    }

    /**
     * 设置选中的item
     *
     * @param pictureItems
     */
    public void setSelectImages(Collection<PictureItem> pictureItems) {
        this.mSelectImages.clear();
        this.mSelectImages.addAll(pictureItems);
        notifyDataSetChanged();
    }


    public void setOnItemSelectedListener(OnItemChangeListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    /*===============================================*/

    public interface OnItemChangeListener {
        /**
         * 点击图片
         *
         * @param selectedPaths 被选中图片
         * @param path          被选中图片路径
         * @param position      position
         */
        void itemClick(ArrayList<PictureItem> selectedPaths, PictureItem path, int position);

        /**
         * 选中回调
         *
         * @param selectedPaths 被选中图片
         */
        void itemSelect(ArrayList<PictureItem> selectedPaths);
    }
}
