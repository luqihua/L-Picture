package com.lu.lib.picture.widget;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.lib.picture.api.IPicCallback;
import com.lu.lib.picture.bean.PictureItem;
import com.lu.lib.picture.crop.CropActivityStart;
import com.lu.lib.picture.crop.CropImage;
import com.lu.lib.picture.ui.AlbumActivity;
import com.lu.lib.picture.util.FileUtil;
import com.lu.lib.picture.util.PhotoOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lu.photo.R;

/**
 * @author luqihua
 * @time 2018/3/6
 * @description 拍照弹窗
 */


public class PictureChooseDialog extends BottomSheetDialog {

    public static final String PHOTO_OPTIONS = "photo_options";
    private static final int TAKE_PHOTO = 0x220;
    private static final int CHOOSE_PHOTO = 0x221;

    private Activity mActivity;
    private IPicCallback mPicCallback;
    private PhotoOptions mPhotoOptions;


    private TextView mTakePhotoView, mCheckPhotoView, mCancelView;
    private List<String> mSelectedPaths = new ArrayList<>();
    private Uri mCameraUri;


    public PictureChooseDialog(Activity activity, PhotoOptions options, IPicCallback callback) {
        super(activity);
        this.mActivity = activity;
        this.mPhotoOptions = options;
        this.mPicCallback = callback;
        initView();
        initListener();
    }

    private void initView() {
        setContentView(R.layout.dialog_choose_picture);
        mTakePhotoView = findViewById(R.id.tv_camera);
        mCheckPhotoView = findViewById(R.id.tv_album);
        mCancelView = findViewById(R.id.tv_cancel);
    }

    private void initListener() {
        /*--------拍照-----*/
        mTakePhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromCamera();
                dismiss();
            }
        });

        /*----从相册选取----*/
        mCheckPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromAlbum();
                dismiss();
            }
        });

        mCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 相册选取
     */
    private void fromAlbum() {
        AlbumActivity.launch(mActivity, mPhotoOptions, CHOOSE_PHOTO);
    }

    /**
     * 拍照
     */
    private void fromCamera() {
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getContext(), "当前手机不支持拍照", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = FileUtil.createImageCacheFile(getContext(), "picture");
        mCameraUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //小于7.0的版本
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
        } else {
            //大于7.0的版本
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mCameraUri.getPath());
            Uri uri = FileUtil.fileUri2ContentUri(mActivity, mCameraUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mActivity.startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * 多张图片压缩
     */
    private void compressPhotos(Collection<String> selectedPaths) {
        PictureCompress.create(getContext())
                .load(selectedPaths)
                .setOnCompressListener(new PictureCompress.OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(List<String> files) {
                        mSelectedPaths.clear();
                        mSelectedPaths.addAll(files);
                        complete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mPicCallback != null) {
                            mPicCallback.error(e.getMessage());
                        }
                    }
                }).start();
    }

    /**
     * 调用系统相机，在对应的activity中的onActivityResult调用该方法
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case CHOOSE_PHOTO:
                mSelectedPaths.clear();
                ArrayList<PictureItem> imageHolders = data.getParcelableArrayListExtra(AlbumActivity.SELECTED_PATHS);
                for (PictureItem imageHolder : imageHolders) {
                    mSelectedPaths.add(imageHolder.getPath());
                }
                if (mPhotoOptions.compress) {
                    compressPhotos(mSelectedPaths);
                } else {
                    complete();
                }
                break;
            case TAKE_PHOTO:
                if (mPhotoOptions.takePhotoCrop) {
                    CropActivityStart.startCrop(mActivity, mPhotoOptions, mCameraUri);
                } else {
                    mSelectedPaths.add(mCameraUri.getPath());
                    complete();
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                mSelectedPaths.clear();
                mSelectedPaths.add(result.getUri().getPath());
                if (mPhotoOptions.compress) {
                    compressPhotos(mSelectedPaths);
                } else {
                    complete();
                }
                break;
        }
    }

    /**
     * 完成操作
     */
    public void complete() {
        if (mPicCallback != null) {
            mPicCallback.success(mSelectedPaths);
        }
    }
}
