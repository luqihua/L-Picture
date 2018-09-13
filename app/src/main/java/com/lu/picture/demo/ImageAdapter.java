package com.lu.picture.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lu.lib.picture.util.DimensionTool;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * author: luqihua
 * date:2018/9/13
 * description:
 **/
public class ImageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mData = new ArrayList<>();
    private int mSize;

    public ImageAdapter(Context context) {
        this.mContext = context;
        mSize = DimensionTool.getScreenWidth() / 3;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(mContext);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(mSize, mSize);
        params.leftMargin = 10;
        params.rightMargin = 10;
        params.topMargin = 10;
        params.bottomMargin = 10;

        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new RecyclerView.ViewHolder(imageView) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final String url = mData.get(position);
        ImageView imageView = (ImageView) holder.itemView;


        Picasso.get().load("file://" + url).into(imageView);
    }


    public void update(Collection<String> data) {
        if (data == null) return;
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }
}
