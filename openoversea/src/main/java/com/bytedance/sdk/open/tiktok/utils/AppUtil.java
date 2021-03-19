package com.bytedance.sdk.open.tiktok.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;


public class AppUtil {

    /**
     * if app is installed
     *
     * @param context
     * @param platformPackageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String platformPackageName) {
        if (context == null || TextUtils.isEmpty(platformPackageName)) {
            return false;
        }
        boolean installed = false;
        if (!TextUtils.isEmpty(platformPackageName)) {
            PackageManager packageManager = context.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(platformPackageName, PackageManager.GET_CONFIGURATIONS);
                if (packageInfo != null) {
                    installed = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return installed;
    }

    public static String buildComponentClassName(String packageName, String classPath) {
        return packageName + "." + classPath;
    }

    /**
     * check app version
     * @param context
     * @param platformPackageName
     * @param remoteRequestEntry
     * @return
     */
    public static int getPlatformSDKVersion(Context context,String platformPackageName, String remoteRequestEntry) {
        if ( context== null || TextUtils.isEmpty(platformPackageName)) {
            return ParamKeyConstants.META_PLATFORM_SDK_VERSION_ERROR;
        }
        if (!AppUtil.isAppInstalled(context, platformPackageName)) {
            return ParamKeyConstants.META_PLATFORM_SDK_VERSION_ERROR;
        }
        try {
            ComponentName componentName = new ComponentName(platformPackageName, AppUtil.buildComponentClassName(platformPackageName, remoteRequestEntry));
            ActivityInfo appInfo = context.getPackageManager().getActivityInfo(componentName, PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                return appInfo.metaData.getInt(ParamKeyConstants.META_PLATFORM_SDK_VERSION, ParamKeyConstants.META_PLATFORM_SDK_VERSION_ERROR);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ParamKeyConstants.META_PLATFORM_SDK_VERSION_ERROR;
    }
}
