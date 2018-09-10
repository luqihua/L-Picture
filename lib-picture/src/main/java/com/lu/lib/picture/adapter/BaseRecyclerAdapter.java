package com.lu.lib.picture.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @Author: luqihua
 * @Time: 2018/6/12
 * @Description: BaseRecyclerAdapter
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected ArrayList<T> mData;
    protected OnPicItemClickListener<T> mItemClickListener;

    public BaseRecyclerAdapter(Context context, ArrayList<T> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = new ArrayList<>(data);
    }

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(getItemLayoutId(viewType), parent, false);
        BaseViewHolder<T> holder = new BaseViewHolder<>(itemView);
        holder.setOnItemClickListener(mItemClickListener);
        onCreateHolder(holder, viewType);
        return holder;
    }

    protected void onCreateHolder(BaseViewHolder<T> holder, int viewType) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseViewHolder) {
            BaseViewHolder<T> baseImageHolder = (BaseViewHolder<T>) holder;
            final T itemData = mData.get(position);
            baseImageHolder.setItemData(itemData);
            bindItemData(baseImageHolder, itemData, position);
        }
    }

    /**
     * 添加一个item
     *
     * @param data
     */
    public void add(T data) {
        if (data == null) return;
        mData.add(data);
        notifyItemChanged(mData.size() - 1);
    }

    /**
     * 添加一组item
     *
     * @param data
     */
    public void insert(Collection<T> data) {
        if (data == null || data.size() == 0) return;
        int oldRange = mData.size();
        mData.addAll(data);
        notifyItemRangeChanged(oldRange, data.size());
    }

    /**
     * 更新数据集合
     *
     * @param data
     */
    public void update(Collection<T> data) {
        if (data == null || data.size() == 0) {
            mData.clear();
        } else if (data != mData) {
            mData.clear();
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     *
     * @param position
     */
    public void remove(int position) {
        if (position < 0 || position >= mData.size()) return;
        mData.remove(position);
        notifyItemRemoved(position);
    }


    public void setItemClickListener(OnPicItemClickListener<T> mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    protected abstract void bindItemData(BaseViewHolder<T> holder, T itemData, int position);

    protected abstract int getItemLayoutId(int viewType);
}
