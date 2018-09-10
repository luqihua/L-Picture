package com.lu.lib.picture.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created  on 2017/5/5.
 * by luqihua
 */

public class PermissionUtil {

    private static Map<String, String> sPermissionMap;

    private static final int REQUEST_CODE = 0x11111;
    private OnPermissionResult onPermissionResult;

    static {
        sPermissionMap = new HashMap<>();
        sPermissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "读写权限");
        sPermissionMap.put(Manifest.permission.CALL_PHONE, "拨打电话权限");
        sPermissionMap.put(Manifest.permission.READ_PHONE_STATE, "读取手机状态权限");
        sPermissionMap.put(Manifest.permission.CAMERA, "摄像头权限");
    }

    /**
     * 申请权限
     *
     * @param activity
     * @param permissions
     * @param result
     */
    public void requestPermission(Activity activity, String[] permissions, OnPermissionResult result) {
        this.onPermissionResult = result;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<String> needPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                    needPermissions.add(permissions[i]);
                }
            }

            if (needPermissions.size() > 0) {
                activity.requestPermissions(needPermissions.toArray(new String[0]), REQUEST_CODE);
            } else {
                if (onPermissionResult != null) {
                    onPermissionResult.onGrant();
                }
            }
        } else {
            if (onPermissionResult != null) {
                onPermissionResult.onGrant();
            }
        }
    }

    public void requestPermission(Fragment fragment, String[] permissons, OnPermissionResult result) {
        this.onPermissionResult = result;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<String> needPermissions = new ArrayList<>();
            for (int i = 0; i < permissons.length; i++) {
                if (ContextCompat.checkSelfPermission(fragment.getActivity(), permissons[i]) == PackageManager.PERMISSION_DENIED) {
                    needPermissions.add(permissons[i]);
                }
            }
            if (needPermissions.size() > 0) {
                fragment.requestPermissions((String[]) needPermissions.toArray(), REQUEST_CODE);
            } else {
                if (onPermissionResult != null) {
                    onPermissionResult.onGrant();
                }
            }
        } else {
            if (onPermissionResult != null) {
                onPermissionResult.onGrant();
            }
        }
    }

    /**
     * 权限申请的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (onPermissionResult == null) return;
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    onPermissionResult.onDeny(sPermissionMap.get(permissions[i]));
                    return;
                }
            }
            onPermissionResult.onGrant();
        }
    }

    public interface OnPermissionResult {
        void onGrant();

        void onDeny(String message);
    }

}
