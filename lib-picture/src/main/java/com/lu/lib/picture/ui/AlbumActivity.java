package com.lu.lib.picture.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.lib.picture.adapter.PictureListAdapter;
import com.lu.lib.picture.bean.PictureFolder;
import com.lu.lib.picture.bean.PictureItem;
import com.lu.lib.picture.util.AlbumScanner;
import com.lu.lib.picture.util.PermissionUtil;
import com.lu.lib.picture.util.PhotoOptions;
import com.lu.lib.picture.widget.PicFolderDialog;
import com.lu.lib.picture.widget.PictureChooseDialog;

import java.util.ArrayList;
import java.util.Map;

import lu.photo.R;

/**
 * @author luqihua
 * @time 2018/6/12
 * @description 图片选择页面
 */
public class AlbumActivity extends AppCompatActivity implements
        PictureListAdapter.OnItemChangeListener {

    public static final String SELECTED_PATHS = "selected_path";

    private RecyclerView rvImageListV;

    private TextView tvFolderNameV;
    private TextView tvPreviewV;
    private TextView tvTitleV;
    private TextView tvCompleteCountV;

    private PermissionUtil mPermissionUtil;
    private PicFolderDialog mImageDirDialog;
    private PictureListAdapter mAdapter;
    //所有的图片，key是文件夹
    private Map<String, ArrayList<PictureItem>> mAllImageMap;
    //当前显示的文件图片集合
    private ArrayList<PictureItem> mCurrentImageList;
    //当前显示的文件夹的名称
    private PictureFolder mCurrentFolder;
    //所有文件夹第一张图片的集合
    private ArrayList<PictureFolder> mImageFolders = new ArrayList<>();

    private PhotoOptions mPhotoOptions;

    private ArrayList<PictureItem> mSelectPaths = new ArrayList<>();

    public static void launch(Activity activity, PhotoOptions options, int requestCode) {
        Intent intent = new Intent(activity, AlbumActivity.class);
        intent.putExtra(PictureChooseDialog.PHOTO_OPTIONS, options);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initPermission();
        handleIntent(getIntent());
        initView();
        initEvent();
    }

    private void initPermission() {
        mPermissionUtil = new PermissionUtil();
        mPermissionUtil.requestPermission(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionUtil.OnPermissionResult() {
                    @Override
                    public void onGrant() {
                        initData();
                    }

                    @Override
                    public void onDeny(String message) {
                        Toast.makeText(AlbumActivity.this, "需要获取读取文件权限，请开启", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionUtil != null) {
            mPermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void handleIntent(Intent intent) {
        mPhotoOptions = intent.getParcelableExtra(PictureChooseDialog.PHOTO_OPTIONS);
    }

    private void initView() {
        rvImageListV = findViewById(R.id.rv_image_list);
        tvFolderNameV = findViewById(R.id.tv_dir_name);
        tvPreviewV = findViewById(R.id.tv_preview);
        tvTitleV = findViewById(R.id.tv_title);
        tvCompleteCountV = findViewById(R.id.tv_complete_count);

        rvImageListV.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new PictureListAdapter(this, mPhotoOptions.targetCount);
        mAdapter.setOnItemSelectedListener(this);
        rvImageListV.setAdapter(mAdapter);
    }

    private void initData() {

        AlbumScanner scanner = new AlbumScanner(this, new AlbumScanner.ScannerCallback() {
            @Override
            public void onPictures(Map<String, ArrayList<PictureItem>> picturesMap) {
                mAllImageMap = picturesMap;
                //图片所在文件夹集合
                for (String key : picturesMap.keySet()) {
                    PictureFolder folder = new PictureFolder();
                    PictureItem holder = picturesMap.get(key).get(0);
                    folder.setFirstImagePath(holder.getPath());
                    folder.setName(key);
                    folder.setCount(picturesMap.get(key).size());
                    mImageFolders.add(folder);
                }
                //从集合中取第一个显示
                mCurrentFolder = mImageFolders.get(0);
                mCurrentImageList = mAllImageMap.get(mCurrentFolder.getName());
                refreshUI();
            }

            @Override
            public void error(String message) {
                Toast.makeText(AlbumActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        scanner.getAllPictures();
    }

    private void initEvent() {
        //预览
        tvPreviewV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(PictureChooseDialog.PHOTO_OPTIONS, mPhotoOptions);
                bundle.putParcelableArrayList(PicturePreviewActivity.INTENT_IMAGE_SELECT, mAdapter.getSelectImages());
                PicturePreviewActivity.startForResult(AlbumActivity.this, bundle);
            }
        });

        //退出
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //完成
        tvCompleteCountV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });

        //文件夹列表弹窗
        findViewById(R.id.ll_dir_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageDirDialog = new PicFolderDialog(AlbumActivity.this, mImageFolders);
                mImageDirDialog.setOnDirSelectedListener(new PicFolderDialog.OnDirSelectedListener() {
                    @Override
                    public void onSelected(PictureFolder folder) {
                        if (mCurrentFolder.getName().equals(folder.getName())) {
                            return;
                        }
                        mCurrentFolder = folder;
                        mCurrentImageList = mAllImageMap.get(mCurrentFolder.getName());
                        refreshUI();
                    }
                });
                mImageDirDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PicturePreviewActivity.REQUEST_PHOTO_PREVIEW) {
            switch (resultCode) {
                case PicturePreviewActivity.RESULT_BACK:
                    mAdapter.setSelectImages(data.<PictureItem>getParcelableArrayListExtra(SELECTED_PATHS));
                    break;
                case PicturePreviewActivity.RESULT_COMPLETE:
                    setResult(RESULT_OK, data);
                    finish();
                    break;
            }
        }
    }

    private void refreshUI() {
        mAdapter.update(mCurrentImageList);
        tvFolderNameV.setText(mCurrentFolder.getName());
        tvTitleV.setText(mCurrentFolder.getName());

        int count = mSelectPaths == null ? 0 : mSelectPaths.size();
        boolean enable = count > 0;

        tvPreviewV.setEnabled(enable);
        tvPreviewV.setText(enable ? String.format("预览(%s/%s)",
                count, mPhotoOptions.targetCount) : "预览");

        tvCompleteCountV.setEnabled(enable);
        tvCompleteCountV.setText(enable ? String.format("完成(%s/%s)",
                count, mPhotoOptions.targetCount) : "完成");
    }

    @Override
    public void itemClick(ArrayList<PictureItem> selectedPaths, PictureItem path,
                          int position) {
        //点击查看大图
        Bundle bundle = new Bundle();
        bundle.putInt(PicturePreviewActivity.INTENT_POSITION_CURRENT, position);
        bundle.putParcelableArrayList(PicturePreviewActivity.INTENT_IMAGE_ALL, mCurrentImageList);
        bundle.putParcelableArrayList(PicturePreviewActivity.INTENT_IMAGE_SELECT, mAdapter.getSelectImages());
        bundle.putParcelable(PictureChooseDialog.PHOTO_OPTIONS, mPhotoOptions);
        PicturePreviewActivity.startForResult(this, bundle);
    }

    @Override
    public void itemSelect(ArrayList<PictureItem> selectedPaths) {
        mSelectPaths = selectedPaths;
        refreshUI();
    }

    private void complete() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(SELECTED_PATHS, mAdapter.getSelectImages());
        setResult(RESULT_OK, intent);
        finish();
    }
}
