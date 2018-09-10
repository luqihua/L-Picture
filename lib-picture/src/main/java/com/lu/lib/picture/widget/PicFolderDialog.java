package com.lu.lib.picture.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lu.lib.picture.adapter.OnPicItemClickListener;
import com.lu.lib.picture.adapter.PicFolderAdapter;
import com.lu.lib.picture.bean.PictureFolder;

import java.util.ArrayList;


/**
 * @author luqihua
 * @time 2018/6/12
 * @description
 */


public class PicFolderDialog extends BottomSheetDialog implements OnPicItemClickListener<PictureFolder> {

    private Context mContext;
    private RecyclerView rvDirListV;
    private ArrayList<PictureFolder> mImageHolders;
    private OnDirSelectedListener mOnDirSelectedListener;

    private PicFolderAdapter mAdapter;

    public PicFolderDialog(Context context, ArrayList<PictureFolder> data) {
        super(context);
        this.mContext = context;
        this.mImageHolders = data;
        this.rvDirListV = getContentView();
        setContentView(rvDirListV);
        initView();
    }

    private RecyclerView getContentView() {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setBackgroundColor(Color.parseColor("#cccccc"));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(params);
        return recyclerView;
    }

    private void initView() {
        rvDirListV.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PicFolderAdapter(mContext, mImageHolders);
        mAdapter.setItemClickListener(this);
        rvDirListV.setAdapter(mAdapter);
    }

    public void setOnDirSelectedListener(OnDirSelectedListener listener) {
        mOnDirSelectedListener = listener;
    }


    @Override
    public void onItemClick(PictureFolder itemData, int position) {
        if (mOnDirSelectedListener != null) {
            mOnDirSelectedListener.onSelected(itemData);
        }
        dismiss();
    }

    public interface OnDirSelectedListener {
        void onSelected(PictureFolder folder);
    }
}
