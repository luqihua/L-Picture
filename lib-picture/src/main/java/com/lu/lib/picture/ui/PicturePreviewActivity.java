package com.lu.lib.picture.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.lu.lib.picture.adapter.OnPicItemClickListener;
import com.lu.lib.picture.adapter.PicThumbAdapter;
import com.lu.lib.picture.adapter.PreviewPagerAdapter;
import com.lu.lib.picture.bean.PictureItem;
import com.lu.lib.picture.crop.CropActivityStart;
import com.lu.lib.picture.crop.CropImage;
import com.lu.lib.picture.util.FileUtil;
import com.lu.lib.picture.util.PhotoOptions;
import com.lu.lib.picture.widget.PictureChooseDialog;

import java.util.ArrayList;
import java.util.Locale;

import lu.photo.R;

/**
 * @author luqihua
 * @time 2018/2/5 下午8:18
 * @description 图片预览
 */

public class PicturePreviewActivity extends AppCompatActivity implements OnPicItemClickListener<PictureItem> {

    public static final int REQUEST_PHOTO_PREVIEW = 0x31;
    public static final int RESULT_BACK = 11;
    public static final int RESULT_COMPLETE = 12;

    public static final String INTENT_IMAGE_SELECT = "intent_image_select";
    public static final String INTENT_IMAGE_ALL = "intent_image_all";
    public static final String INTENT_POSITION_CURRENT = "intent_position_current";
    public static final String INTENT_IS_SELECTED = "intent_is_selected";

    private AppCompatCheckedTextView ctvPicSelectV;
    private ViewPager mViewPager;
    private TextView tvTitleV;
    private TextView tvCompleteCountV;
    private TextView tvEdit;//编辑
    private RecyclerView rvPicThumbV;//小图列表


    private PreviewPagerAdapter mPagerAdapter;
    private PicThumbAdapter mThumbAdapter;

    private PhotoOptions mPhotoOptions;
    private ArrayList<PictureItem> mSelectedPictures;
    private ArrayList<PictureItem> mAllPictures;
    private int mCurrentPosition;

    public static void startForResult(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, PicturePreviewActivity.class);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, REQUEST_PHOTO_PREVIEW);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        handlerIntent(getIntent());
        initView();
        initListener();
    }

    private void handlerIntent(Intent intent) {
        mSelectedPictures = intent.getParcelableArrayListExtra(INTENT_IMAGE_SELECT);
        mAllPictures = intent.getParcelableArrayListExtra(INTENT_IMAGE_ALL);
        //mAllPictures为空的时候是预览已选中的照片
        //mAllPictures不为空时预览所有的照片
        if (mAllPictures == null) {
            mAllPictures = new ArrayList<>(mSelectedPictures);
        }
        mPhotoOptions = intent.getParcelableExtra(PictureChooseDialog.PHOTO_OPTIONS);
        mCurrentPosition = intent.getIntExtra(INTENT_POSITION_CURRENT, 0);
    }

    private void initView() {
        //编辑按钮
        tvEdit = findViewById(R.id.tv_edit);
        tvEdit.setVisibility(mPhotoOptions.selectPhotoCrop ? View.VISIBLE : View.GONE);

        tvTitleV = findViewById(R.id.tv_title);
        tvTitleV.setText(getTitle(mCurrentPosition));

        tvCompleteCountV = findViewById(R.id.tv_complete_count);

        //选择按钮初始化
        ctvPicSelectV = findViewById(R.id.ctv_check);
        ctvPicSelectV.setChecked(mSelectedPictures.contains(mAllPictures.get(mCurrentPosition)));

        //图片轮播初始化
        mViewPager = findViewById(R.id.view_pager);
        mPagerAdapter = new PreviewPagerAdapter(this, mAllPictures);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);

        //小图列表初始化
        rvPicThumbV = findViewById(R.id.rv_selected_picture);
        rvPicThumbV.getItemAnimator().setChangeDuration(0);
        rvPicThumbV.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        mThumbAdapter = new PicThumbAdapter(this, mSelectedPictures);
        mThumbAdapter.setCurrentItem(mAllPictures.get(mCurrentPosition));
        rvPicThumbV.setAdapter(mThumbAdapter);

        refreshUI();
    }

    private void initListener() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beBack();
            }
        });

        tvCompleteCountV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhoto();
            }
        });

        ctvPicSelectV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureItem holder = mAllPictures.get(mCurrentPosition);
                if (mThumbAdapter.containData(holder)) {
                    ctvPicSelectV.setChecked(false);
                    mThumbAdapter.removeData(holder);
                    refreshUI();
                } else if (mThumbAdapter.getItemCount() < mPhotoOptions.targetCount) {
                    ctvPicSelectV.setChecked(true);
                    mThumbAdapter.addData(holder);
                    refreshUI();
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                PictureItem holder = mAllPictures.get(position);
                mThumbAdapter.setCurrentItem(holder);
                ctvPicSelectV.setChecked(mSelectedPictures.contains(holder));
                tvTitleV.setText(getTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mThumbAdapter.setItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            String resultPath = result.getUri().getPath();
            final PictureItem preImage = mAllPictures.get(mCurrentPosition);
            preImage.setPath(resultPath);
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            beBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回上个界面
     */
    private void beBack() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(AlbumActivity.SELECTED_PATHS, mThumbAdapter.getData());
        setResult(RESULT_BACK, intent);
        finish();
    }

    /**
     * 点击完成
     */
    private void complete() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(AlbumActivity.SELECTED_PATHS, mThumbAdapter.getData());
        setResult(RESULT_COMPLETE, intent);
        finish();
    }

    /**
     * 编辑照片
     */
    private void editPhoto() {
        String path = mAllPictures.get(mCurrentPosition).getPath();
        Uri uri = FileUtil.fileUri2ContentUri(this, path);
        CropActivityStart.startCrop(this, mPhotoOptions, uri);
    }

    private void refreshUI() {
        int count = mThumbAdapter.getItemCount();
        tvCompleteCountV.setEnabled(count > 0);
        tvCompleteCountV.setText(count > 0 ? String.format("完成(%s/%s)", count, mPhotoOptions.targetCount) : "完成");
    }


    private String getTitle(int position) {
        return String.format(Locale.CHINA, "%d/%d", position + 1, mAllPictures.size());
    }

    /**
     * 缩略图选中
     *
     * @param itemData
     * @param position
     */

    @Override
    public void onItemClick(PictureItem itemData, int position) {
        mViewPager.setCurrentItem(mAllPictures.indexOf(itemData));
    }
}
