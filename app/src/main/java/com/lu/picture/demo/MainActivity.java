package com.lu.picture.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.lu.lib.picture.LPicture;
import com.lu.lib.picture.api.IPicCallback;
import com.lu.lib.picture.util.PhotoOptions;
import com.lu.lib.picture.widget.PictureChooseDialog;

import java.util.List;


public class MainActivity extends AppCompatActivity {


    private RecyclerView rvImageListV;
    private ImageAdapter mAdapter;
    private PictureChooseDialog mPhotoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initView() {
        rvImageListV = findViewById(R.id.rv_image_list);
        rvImageListV.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new ImageAdapter(this);
        rvImageListV.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPhotoDialog != null) {
            mPhotoDialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void choose(View v) {

        PhotoOptions options = new PhotoOptions.Builder()
                .setMaxCount(6)//目标数量
                .setCurrentCount(mAdapter.getItemCount())//当前已有数量
                .setCompress(true)//选中图片后是否进行压缩
                .build();
        mPhotoDialog = LPicture.getInstance().createPicDialog(this, options, new IPicCallback() {
            @Override
            public void success(List<String> paths) {
                mAdapter.update(paths);
            }

            @Override
            public void error(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        mPhotoDialog.show();
    }
}
