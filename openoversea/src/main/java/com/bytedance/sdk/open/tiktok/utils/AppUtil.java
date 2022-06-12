package com.bytedance.sdk.open.tiktok.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.common.constants.Keys;


public class AppUtil {


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
            return Keys.SDK_VERSION_ERROR;
        }
        try {
            ComponentName componentName = new ComponentName(platformPackageName, AppUtil.buildComponentClassName(platformPackageName, remoteRequestEntry));
            ActivityInfo appInfo = context.getPackageManager().getActivityInfo(componentName, PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                return appInfo.metaData.getInt(Keys.SDK_VERSION_KEY, Keys.SDK_VERSION_ERROR);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Keys.SDK_VERSION_ERROR;
    }
}
