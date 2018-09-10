package com.lu.lib.picture.util;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.lu.lib.picture.bean.PictureItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created  on 2017/4/24.
 * by luqihua
 * 用于扫描手机内所有照片的工具类
 */

public class AlbumScanner {
    private static final String[] STORE_IMAGES = {
            /*图片完整路径*/
            MediaStore.Images.Media.DATA,
            /*文件名称包含后缀*/
            MediaStore.Images.Media.DISPLAY_NAME,
            /*所在文件夹名称*/
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            /*文件名不包含后缀*/
            MediaStore.Images.Media.TITLE
    };

    private Context mContext;
    private ScannerHandler mHandler;
    private ScannerCallback mScannerCallback;

    public AlbumScanner(Context context, ScannerCallback callback) {
        this.mContext = context;
        this.mScannerCallback = callback;
        this.mHandler = new ScannerHandler(callback);
    }

    public void getAllPictures() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (mScannerCallback != null) {
                mScannerCallback.error("当前存储卡不可用");
            }
            return;
        }

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = MediaStore.Images.Media
                        .query(mContext.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);

                Map<String, ArrayList<PictureItem>> listMap = new HashMap<>();

                while (cursor.moveToNext()) {

                    String path = cursor.getString(0);
                    String name = cursor.getString(1);
                    String bucketName = cursor.getString(2);
                    String title = cursor.getString(3);

                    PictureItem holder = new PictureItem(path, name, bucketName, title);

                    ArrayList<PictureItem> holders = listMap.get(bucketName);
                    if (holders == null) {
                        holders = new ArrayList<>();
                        listMap.put(bucketName, holders);
                    }

                    holders.add(holder);
                }
                mHandler.obtainMessage(0, listMap).sendToTarget();
            }
        });
    }

    /*======================================================*/
    private static class ScannerHandler extends Handler {

        private WeakReference<ScannerCallback> callbackWeakReference;

        public ScannerHandler(ScannerCallback callback) {
            this.callbackWeakReference = new WeakReference<ScannerCallback>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            ScannerCallback callback = callbackWeakReference.get();
            if (callback == null) return;
            Map<String, ArrayList<PictureItem>> picturesMap = (Map<String, ArrayList<PictureItem>>) msg.obj;
            callback.onPictures(picturesMap);
        }
    }

    /*======================================================*/
    public interface ScannerCallback {
        void onPictures(Map<String, ArrayList<PictureItem>> picturesMap);

        void error(String message);
    }
}
