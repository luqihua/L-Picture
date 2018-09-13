package com.lu.lib.picture.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lu.lib.picture.crop.CropImage;
import com.lu.lib.picture.crop.CropImageOptions;
import com.lu.lib.picture.crop.CropImageView;
import com.lu.lib.picture.util.FileUtil;

import java.io.File;

import lu.photo.R;

/**
 * @author luqihua
 * @time 2018/2/8 下午11:08
 * @description 照片编辑
 */
public class PictureEditActivity extends AppCompatActivity implements
        CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener {

    private CropImageView mCropImageView;
    private CropImageOptions mOptions;

    private Uri mCropImageUri;
    private boolean completed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_edit);
        handleIntent(getIntent());
        initView();
        initListener();
    }

    private void handleIntent(Intent intent) {
        mCropImageUri = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_SOURCE);
        mOptions = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_OPTIONS);
    }

    private void initView() {
        mCropImageView = findViewById(R.id.cropImageView);

        mCropImageView.setAspectRatio(mOptions.aspectRatioX, mOptions.aspectRatioY);
        mCropImageView.setFixedAspectRatio(mOptions.fixAspectRatio);
        mCropImageView.setImageUriAsync(mCropImageUri);
    }

    private void initListener() {
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);

        findViewById(R.id.iv_edit_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.iv_edit_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage(-mOptions.rotationDegrees);
            }
        });
        findViewById(R.id.tv_edit_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!completed) {
            setResultCancel();
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            if (mOptions.initialCropWindowRectangle != null) {
                mCropImageView.setCropRect(mOptions.initialCropWindowRectangle);
            }
            if (mOptions.initialRotation > -1) {
                mCropImageView.setRotatedDegrees(mOptions.initialRotation);
            }
        } else {
            setResult(null, error, 1);
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        FileUtil.galleryAddPic(this, result.getUri());
        setResult(result.getUri(), result.getError(), result.getSampleSize());
    }

    /**
     * 剪裁图片，保存到指定地址
     */
    protected void cropImage() {
        if (mOptions.noOutputImage) {
            setResult(null, null, 1);
        } else {
            Uri outputUri = getOutputUri();
            mCropImageView.saveCroppedImageAsync(
                    outputUri,
                    mOptions.outputCompressFormat,
                    mOptions.outputCompressQuality,
                    mOptions.outputRequestWidth,
                    mOptions.outputRequestHeight,
                    mOptions.outputRequestSizeOptions);
        }
    }

    /**
     * 旋转图片
     */
    protected void rotateImage(int degrees) {
        mCropImageView.rotateImage(degrees);
    }

    protected Uri getOutputUri() {
        Uri outputUri = mOptions.outputUri;
        if (outputUri == null || outputUri.equals(Uri.EMPTY)) {
            File file = FileUtil.createImageCacheFile( this, "cropped", mOptions.outputCompressFormat);
            outputUri = Uri.fromFile(file);
        }
        return outputUri;
    }

    protected void setResult(Uri uri, Exception error, int sampleSize) {
        int resultCode = error == null ? RESULT_OK : CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE;
        setResult(resultCode, getResultIntent(uri, error, sampleSize));
        onBackPressed();
    }

    protected void setResultCancel() {
        setResult(RESULT_CANCELED);
    }

    protected Intent getResultIntent(Uri uri, Exception error, int sampleSize) {
        completed = true;
        CropImage.ActivityResult result =
                new CropImage.ActivityResult(
                        mCropImageView.getImageUri(),
                        uri,
                        error,
                        mCropImageView.getCropPoints(),
                        mCropImageView.getCropRect(),
                        mCropImageView.getRotatedDegrees(),
                        mCropImageView.getWholeImageRect(),
                        sampleSize);
        Intent intent = new Intent();
        intent.putExtras(getIntent());
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result);
        intent.putExtra(PicturePreviewActivity.INTENT_IS_SELECTED, mOptions.selected);
        return intent;
    }


}
