package com.bytedance.sdk.open.aweme.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
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
}
