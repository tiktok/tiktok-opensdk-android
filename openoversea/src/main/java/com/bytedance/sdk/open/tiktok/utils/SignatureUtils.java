package com.bytedance.sdk.open.tiktok.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


public class SignatureUtils {

    /**
     * verify signature
     *
     * @param context
     * @param pkgName
     * @param sign
     * @return
     */
    public static boolean validateSign(Context context, String pkgName, String sign) {
        if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(sign) || context == null) {
            return false;
        } else {
            List<String> signList = getMd5Signs(context, pkgName);
            if (signList != null && signList.size() > 0) {
                for (String appSign : signList) {
                    if (sign.equalsIgnoreCase(appSign)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * get app signature
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static List<String> getMd5Signs(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        } else {
            PackageInfo packageInfo;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException var4) {
                return null;
            }
            List<String> signList = new ArrayList<>();
            Signature[] signatures = packageInfo.signatures;
            if (null != signatures) {
                Signature[] var2 = signatures;
                int var3 = signatures.length;
                for (int var4 = 0; var4 < var3; ++var4) {
                    Signature signature = var2[var4];
                    String s = Md5Utils.hexDigest(signature.toByteArray());
                    signList.add(s);
                }
                return signList;
            }
        }
        return null;
    }

    /**
     *
     * Joining signatures in an array into a string
     * @param signs
     * @return
     */
    public static String packageSignature(List<String> signs) {
        if (signs != null && !signs.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < signs.size(); i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(signs.get(i));
            }
            return sb.toString();
        }
        return null;
    }
}
