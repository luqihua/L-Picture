package com.lu.picture.demo;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.lu.lib.picture.LPicture;
import com.lu.lib.picture.api.IPictureLoader;
import com.squareup.picasso.Picasso;

/**
 * author: luqihua
 * date:2018/9/13
 * description:
 **/
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LPicture.getInstance().init(new IPictureLoader() {
            @Override
            public ImageView createImageView(Context context) {
                return new ImageView(context);
            }

            @Override
            public void displayImage(Context context, ImageView view, String path) {
                Picasso.get().load("file://"+path).into(view);
            }
        });
    }
}
