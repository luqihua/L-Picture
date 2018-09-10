package com.lu.lib.picture.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author luzeyan
 * @time 2018/2/5 下午7:43
 * @description 图片压缩
 */
public class PictureCompress implements Handler.Callback {

    private static final String DEFAULT_CACHE_DIR = "compressed";

    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;

    private List<String> mPaths;

    private String mTagDir;
    private Handler mHandler;
    private OnCompressListener mOnCompressListener;

    /**
     * 固定输出大小 KB
     */
    private int mSize;

    @Override
    public boolean handleMessage(Message msg) {
        if (mOnCompressListener == null) {
            return false;
        }
        switch (msg.what) {
            case MSG_COMPRESS_START:
                mOnCompressListener.onStart();
                break;
            case MSG_COMPRESS_SUCCESS:
                mOnCompressListener.onSuccess((List<String>) msg.obj);
                break;
            case MSG_COMPRESS_ERROR:
                mOnCompressListener.onError((Throwable) msg.obj);
                break;
            default:
        }
        return false;
    }

    private PictureCompress(Builder builder) {
        mPaths = builder.paths;
        mTagDir = builder.tagFile;
        mSize = builder.size;
        mOnCompressListener = builder.onCompressListener;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder create(Context context) {
        return new Builder(context);
    }


    private void start(final Context context) {
        if (mPaths == null || mPaths.size() == 0) {
            return;
        }

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                List<String> files = new ArrayList<>();
                mHandler.obtainMessage(MSG_COMPRESS_START).sendToTarget();
                for (final String path : mPaths) {
                    if (isImage(path)) {
                        try {
                            files.add(compress(context, path));
                        } catch (IOException e) {
                            mHandler.obtainMessage(MSG_COMPRESS_ERROR, e).sendToTarget();
                        }
                    }
                }
                mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, files).sendToTarget();

            }
        });

    }

    /**
     * @param context context
     */
    @WorkerThread
    private String compress(Context context, String path) throws IOException {
        if (isNeedCompress(mSize, path)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 1;

            BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight);
            options.inJustDecodeBounds = false;

            //默认压缩质量
            int quality = 60;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            bitmap = rotatingImage(bitmap, path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

            long cSize = stream.toByteArray().length;
            //压缩到固定大小,不设置则默认60
            while (cSize > mSize && quality > 6) {
                if (cSize > mSize * 4) {
                    quality -= 20;
                } else if (cSize > mSize * 2) {
                    quality -= 10;
                } else {
                    quality -= 5;
                }
                stream.reset();//重置缓冲区
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                cSize = stream.toByteArray().length >> 10;
            }
            bitmap.recycle();

            File file = createImageCacheFile(context);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
            stream.close();

            return file.getPath();
        }

        return path;
    }

    @WorkerThread
    private String get(Context context, String path) throws IOException {
        return compress(context, path);
    }

    @WorkerThread
    private List<String> get(Context context, List<String> paths) throws IOException {
        List<String> files = new ArrayList<>();
        for (String path : paths) {
            files.add(compress(context, path));
        }
        return files;
    }


    public static class Builder {

        private Context context;
        private String tagFile;
        private List<String> paths;
        private OnCompressListener onCompressListener;
        private int size;

        private Builder(Context context) {
            this.context = context;
            this.paths = new ArrayList<>();
        }

        /**
         * 图片压缩最小值
         *
         * @param size file size，unit KB，default 100K
         */
        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setOnCompressListener(OnCompressListener onCompressListener) {
            this.onCompressListener = onCompressListener;
            return this;
        }

        public Builder load(File file) {
            this.paths.add(file.getAbsolutePath());
            return this;
        }

        public Builder load(String path) {
            this.paths.add(path);
            return this;
        }

        public Builder load(Collection<String> list) {
            this.paths.addAll(list);
            return this;
        }

        /**
         * 设置压缩后文件存放位置
         *
         * @param path 输出文件夹路径
         */
        public Builder setTagFile(String path) {
            this.tagFile = path;
            return this;
        }

        /**
         * ui 线程
         */
        public String get(String path) throws IOException {
            return build().get(context, path);
        }

        /**
         * ui 线程
         */
        public List<String> get(List<String> paths) throws IOException {
            return build().get(context, paths);
        }


        private PictureCompress build() {
            return new PictureCompress(this);
        }

        public void start() {
            build().start(context);
        }
    }


    /**
     * 计算压缩尺寸
     *
     * @param width  width
     * @param height height
     */
    private int calculateSampleSize(int width, int height) {
        width = width % 2 == 1 ? width + 1 : width;
        height = height % 2 == 1 ? height + 1 : height;

        int longSide = Math.max(width, height);
        int shortSide = Math.min(width, height);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide >= 1664 && longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    /**
     * 旋转图片
     */
    private Bitmap rotatingImage(Bitmap bitmap, String path) throws IOException {
        ExifInterface mSrcExif = new ExifInterface(path);
        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = mSrcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            default:
        }
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private boolean isImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        String suffix = path.substring(path.lastIndexOf(".") + 1, path.length());
        return "jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix) ||
                "png".equalsIgnoreCase(suffix) || "webp".equalsIgnoreCase(suffix);
    }

    private File createImageCacheFile(Context context) {
        if (TextUtils.isEmpty(mTagDir)) {
            mTagDir = createImageCacheDir(context).getAbsolutePath();
        }

        String cacheBuilder = mTagDir + "/" + System.currentTimeMillis() +
                new Random().nextInt(1000) + ".jpg";

        return new File(cacheBuilder);
    }

    private File createImageCacheDir(Context context) {
        return createImageCacheDir(context, DEFAULT_CACHE_DIR);
    }

    private File createImageCacheDir(Context context, String cacheName) {
        File file = new File(context.getExternalCacheDir(), cacheName);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    private boolean isNeedCompress(int leastCompressSize, String path) {
        if (leastCompressSize > 0) {
            File source = new File(path);
            if (!source.exists()) {
                return false;
            }

            if (source.length() > (leastCompressSize << 10)) {
                return true;
            }
        }
        return false;
    }

    public interface OnCompressListener {
        void onStart();

        void onSuccess(List<String> files);

        void onError(Throwable e);
    }

}
