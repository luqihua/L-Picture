package com.lu.lib.picture.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author luqihua
 * @time 2018/2/6
 * @description
 */


public class FileUtil {

    public static FilenameFilter getImgFileFilter() {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
            }
        };
    }

    public static ArrayList<String> getDirImgFiles(File dir) {
        ArrayList<String> paths = new ArrayList<>();
        List<String> list = Arrays.asList(dir.list(getImgFileFilter()));
        String absolutePath = dir.getAbsolutePath();
        for (String fileName : list) {
            paths.add(absolutePath + "/" + fileName);
        }
        return paths;
    }

    /**
     * 创建图片文件，默认jpeg格式
     * @param context
     * @param dirName 文件名称
     * @return
     */
    public static File createImageCacheFile(Context context, String dirName) {
        return createImageCacheFile(context, dirName, Bitmap.CompressFormat.JPEG);
    }


    /**
     * 创建图片文件
     * @param context
     * @param dirName
     * @param format 图片类型
     * @return
     */
    public static File createImageCacheFile(Context context, String dirName, Bitmap.CompressFormat format) {
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName(), dirName);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        return new File(file, createFileName(format));
    }

    /**
     * 转换 content:// uri
     */
    public static Uri fileUri2ContentUri(Context context, Uri uri) {
        return fileUri2ContentUri(context, uri.getPath());
    }


    /**
     * 生成 content:// 类型uri
     */
    public static Uri fileUri2ContentUri(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    /**
     * 调用系统的扫描器将图片添加到媒体扫描器的数据库中，
     * 使得这些照片可以被系统的相册应用或者其他app访问
     */
    public static void galleryAddPic(Context context, Uri uri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        context.sendBroadcast(mediaScanIntent);
    }


    /**
     * 随机生成一个图片文件名称
     * @param format
     * @return
     */
    private static String createFileName(Bitmap.CompressFormat format) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'IMG'_MM_dd_HHmmss", Locale.CHINA);
        return sdf.format(date) + "." + format.toString();
    }

}
