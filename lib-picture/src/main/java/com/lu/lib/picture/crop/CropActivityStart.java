package com.lu.lib.picture.crop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.lu.lib.picture.ui.PictureEditActivity;
import com.lu.lib.picture.util.PhotoOptions;

/**
 * @Author: luqihua
 * @Time: 2018/6/11
 * @Description: CropActivityStart
 */

public class CropActivityStart {
    public static void startCrop(Activity activity, PhotoOptions photoOptions, Uri uri) {
        Intent intent = new Intent(activity, PictureEditActivity.class);
        CropImageOptions options = new CropImageOptions();
        options.aspectRatioX = photoOptions.aspectRatioX;
        options.aspectRatioY = photoOptions.aspectRatioY;
        options.fixAspectRatio = photoOptions.fixAspectRatio;

        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_OPTIONS, options);
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_SOURCE, uri);
        activity.startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }
}
