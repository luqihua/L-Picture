package com.lu.lib.picture.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.lu.lib.picture.LPicture;
import com.lu.lib.picture.bean.PictureItem;
import com.lu.lib.picture.widget.photoview.PhotoView;

import java.util.List;


/**
 * @author luzeyan
 * @time 2018/2/8 上午10:39
 * @description
 */

public class PreviewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<PictureItem> mData;
    private SparseArray<PhotoView> mViews = new SparseArray<>();

    public PreviewPagerAdapter(Context context, List<PictureItem> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView itemView = mViews.get(position);
        if (itemView == null || itemView.getParent() != null) {
            itemView = createItemView();
            mViews.put(position, itemView);
        }
        LPicture.getInstance()
                .getPicLoader()
                .displayImage(mContext, itemView, mData.get(position).getPath());
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    private PhotoView createItemView() {
        PhotoView photoView = new PhotoView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setLayoutParams(params);
        return photoView;
    }
}
