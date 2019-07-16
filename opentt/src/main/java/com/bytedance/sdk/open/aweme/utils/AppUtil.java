package com.bytedance.sdk.open.aweme.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;

/**
 * Created by yangzhirong on 2018/10/8.
 */
public class AppUtil {

    /**
     * 应用是否安装
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
                PackageInfo packageInfo = packageManager.getPackageInfo(platformPackageName, PackageManager.GET_ACTIVITIES);
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
     * 打开目标应用
     */
    public static boolean openApp(Context context, String platformPackageName) {
        if (!isAppInstalled(context, platformPackageName)) {
            return false;
        } else if (context == null) {
            return false;
        } else {
            try {
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(platformPackageName));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * 检测抖音版本
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
