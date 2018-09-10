package com.lu.lib.picture.api;

import android.content.Context;
import android.widget.ImageView;

/**
 * @Author: luqihua
 * @Time: 2018/6/11
 * @Description: IPictureLoader
 */

public interface IPictureLoader {
    ImageView createImageView(Context context);

    void displayImage(Context context, ImageView view, String path);
}
