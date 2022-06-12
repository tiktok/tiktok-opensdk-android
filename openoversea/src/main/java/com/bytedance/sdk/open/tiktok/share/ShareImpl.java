package com.bytedance.sdk.open.tiktok.share;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.common.constants.Keys;
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler;
import com.bytedance.sdk.open.tiktok.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;


public class ShareImpl {

    private Context mContext;
    private List<IDataHandler> handlers = new ArrayList<>();
    private String mClientKey;

    public ShareImpl(Context context, String clientKey) {
        this.mContext = context;
        this.mClientKey = clientKey;
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
    public boolean share(String localEntry, String remotePackageName, String remoteRequestEntry, Share.Request request,String remotePlatformEntryName, String sdkName, String sdkVersion) {
        if (TextUtils.isEmpty(remotePackageName) || request == null || mContext == null) {
            return false;
        } else if (!request.checkArgs()) {
            return false;
        } else {
            // packages
            Bundle bundle = new Bundle();

            if (AppUtils.Companion.getPlatformSDKVersion(mContext, remotePackageName,remotePlatformEntryName)
                    >= Keys.API.MIN_SDK_NEW_VERSION_API) {
                request.toBundle(bundle);
            }
            bundle.putString(Keys.Share.CLIENT_KEY, mClientKey);
            bundle.putString(Keys.Share.CALLER_PKG, mContext.getPackageName());
            bundle.putString(Keys.Share.CALLER_SDK_VERSION, Keys.VERSION);

            if (TextUtils.isEmpty(request.callerLocalEntry)) {
                bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, "com.bytedance.sdk.open.tiktok" + "." + "TikTokShareResponseActivity");

//                bundle.putString(Keys.Share.CALLER_LOCAL_ENTRY, mContext.getPackageName() + "." +localEntry);
            }

            if (request.extras != null) {
                bundle.putBundle(Keys.Base.EXTRA, request.extras);
            }
            bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, sdkName);
            bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, sdkVersion);

            bundle.putString(Keys.Share.OPENPLATFORM_EXTRA, request.mExtra);
            bundle.putString(Keys.Share.ANCHOR_SOURCE_TYPE, request.mAnchorSourceType);
            bundle.putSerializable(Keys.Share.EXTRA_SHARE_OPTIONS, request.extraShareOptions);
            bundle.putInt(Keys.Share.SHARE_FORMAT, request.mShareFormat.getValue());

            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(remotePackageName, buildComponentClassName(remotePackageName, remoteRequestEntry));
            intent.setComponent(componentName);
            intent.putExtras(bundle);


            if (!(mContext instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
