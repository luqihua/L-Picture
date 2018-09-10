package com.lu.lib.picture.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
/**
 * @Author: luqihua
 * @Time: 2018/6/12
 * @Description: 基类RecyclerView.ViewHolder
 */
public class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {

    private SparseArray<View> mViews = new SparseArray<View>();
    //item的点击监听
    private OnPicItemClickListener<T> mListener = null;
    // holder绑定的数据源对象
    private T mItemData;
    // 在adapter中的位置
    private int position;

    public BaseViewHolder(View v) {
        super(v);
        this.itemView.setOnClickListener(this);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setItemData(T itemData) {
        this.mItemData = itemData;
    }

    public void setOnItemClickListener(OnPicItemClickListener<T> listener) {
        this.mListener = listener;
    }

    public T getItemData() {
        return mItemData;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(mItemData, position);
        }
    }

    public <E extends View> E getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (E) view;
    }

    public void setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
    }

    public <E extends View> E getItemView() {
        return (E) this.itemView;
    }
}