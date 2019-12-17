package com.bytedance.sdk.open.aweme.share;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.handler.TikTokDataHandler;
import com.bytedance.sdk.open.aweme.TikTokOpenConfig;
import com.bytedance.sdk.open.aweme.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by WangJiaWei on 2019/1/15.
 */
public class ShareImpl {

    private Context mContext;
    private TikTokOpenConfig openConfig;
    private List<TikTokDataHandler> handlers = new ArrayList<>();

    public ShareImpl(Context context, TikTokOpenConfig sConfig) {
        this.mContext = context;
        this.openConfig = sConfig;
        this.handlers.add(new ShareDataHandler());
    }

    /**
     * send a request to target app
     *
     * @param remotePackageName  target app's package name
     * @param remoteRequestEntry target app's entry actity name
     * @param request            request data
     * @param localEntry         your app's activity name to get result from target app
     * @return
     */
    public boolean share(String localEntry, String remotePackageName, String remoteRequestEntry, Share.Request request,String remotePlatformEntryName) {
        if (TextUtils.isEmpty(remotePackageName) || request == null || mContext == null) {
            return false;
        } else if (!request.checkArgs()) {
            return false;
        } else {
            // packages
            Bundle bundle = new Bundle();

            if (AppUtil.getPlatformSDKVersion(mContext, remotePackageName,remotePlatformEntryName)
                    >= ParamKeyConstants.REQUIRED_API_VERSION.MIN_SDK_NEW_TIKTOK_API) {
                request.toBundle(bundle);
            } else {
                request.toBundleForOldVersion(bundle);
            }
            bundle.putString(ParamKeyConstants.ShareParams.CLIENT_KEY, openConfig.clientKey);
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_PKG, mContext.getPackageName());
            bundle.putString(ParamKeyConstants.ShareParams.CALLER_SDK_VERSION, ParamKeyConstants.SdkVersion.VERSION);
            if (TextUtils.isEmpty(request.callerLocalEntry)) {
                bundle.putString(ParamKeyConstants.ShareParams.CALLER_LOCAL_ENTRY, mContext.getPackageName() + "." + localEntry);
            }

            if (request.extras != null) {
                bundle.putBundle(ParamKeyConstants.BaseParams.EXTRA, request.extras);
            }

            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(remotePackageName, buildComponentClassName(remotePackageName, remoteRequestEntry));
            intent.setComponent(componentName);
            intent.putExtras(bundle);


            if (mContext instanceof Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            try {
                mContext.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private String buildComponentClassName(String packageName, String classPath) {
        return "com.ss.android.ugc.aweme" + "." + classPath;
    }
}
