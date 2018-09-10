package com.lu.lib.picture.util;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @Author: luqihua
 * @Time: 2018/6/12
 * @Description: DimensionTool
 */

public class DimensionTool {

    private static final DisplayMetrics S_METRICS;

    static {
        S_METRICS = Resources.getSystem().getDisplayMetrics();
    }

    /**
     * @return 屏幕宽度(px)
     */
    @SuppressLint("NewApi")
    public static int getScreenWidth() {
        return S_METRICS.widthPixels;
    }

    /**
     * @return 获取屏幕高度(px)
     */
    @SuppressLint("NewApi")
    public static int getScreenHeight() {
        return S_METRICS.heightPixels;
    }

    /**
     * 根据手机分辨率将dp的单位转换为px
     *
     * @param dpValue
     * @return
     */

    public static int dp2px(float dpValue) {
        final float scale = S_METRICS.density;
        return (int) (dpValue * scale + 0.5f);// 四舍五入
    }

    /**
     * 根据手机分辨率将px的单位转换为dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dp(float pxValue) {
        final float scale = S_METRICS.density;

        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float scale = S_METRICS.density;
        return (int) (pxValue / scale + 0.5);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float scale = S_METRICS.scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }
}
