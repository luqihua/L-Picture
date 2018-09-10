package com.lu.lib.picture;

import android.app.Activity;

import com.lu.lib.picture.api.IPictureLoader;
import com.lu.lib.picture.api.IPicCallback;
import com.lu.lib.picture.util.PhotoOptions;
import com.lu.lib.picture.widget.PictureChooseDialog;

/**
 * @Author: luqihua
 * @Time: 2018/6/12
 * @Description: LPicture
 */

public class LPicture {
    private boolean hasInitialize;
    private IPictureLoader mPicLoader;

    LPicture() {
    }

    private static class Holder {
        private static LPicture sInstance = new LPicture();
    }

    public static LPicture getInstance() {
        return Holder.sInstance;
    }

    public void init(IPictureLoader loader) {
        this.mPicLoader = loader;
        this.hasInitialize = true;
    }

    public IPictureLoader getPicLoader() {
        return mPicLoader;
    }

    public PictureChooseDialog createPicDialog(Activity activity, PhotoOptions options, IPicCallback callback) {
        if (!hasInitialize) {
            if (callback != null) {
                callback.error("LPicture：必须先调用init()方法初始化");
            } else {
                throw new RuntimeException("LPicture：必须先调用init()方法初始化");
            }
        }
        return new PictureChooseDialog(activity, options, callback);
    }
}
