package com.bytedance.sdk.account.open.aweme.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.account.bdopen.api.BDBaseOpenBuildConstants;
import com.bytedance.sdk.account.bdopen.impl.BDOpenConfig;
import com.bytedance.sdk.account.open.aweme.DYOpenConstants;

/**
 * Powered by WangJiaWei on 2019/1/15.
 */
public class ShareImpl {

    private Context mContext;
    private BDOpenConfig openConfig;

    public ShareImpl(Context context, BDOpenConfig sConfig) {
        mContext = context;
        openConfig = sConfig;
    }

    /**
     * 发送一个 Request 请求到目标应用
     *
     * @param remotePackageName  目标应用包名
     * @param remoteRequestEntry 目标应用入口 Activity. 用于接收 Request
     * @param request            实际请求数据
     * @param localEntry         当前应用的入口 Activity.用于接收 Response
     * @return
     */
    public boolean share(String localEntry, String remotePackageName, String remoteRequestEntry, Share.Request request) {
        if (TextUtils.isEmpty(remotePackageName) || request == null || mContext == null) {
            return false;
        } else if (!request.checkArgs()) {
            return false;
        } else {
            // packages
            Bundle bundle = new Bundle();
            request.toBundle(bundle);
            bundle.putString(DYOpenConstants.Params.CLIENT_KEY, openConfig.clientKey);
            bundle.putString(DYOpenConstants.Params.CALLER_PKG, mContext.getPackageName());
            bundle.putString(DYOpenConstants.Params.CALLER_SDK_VERSION, BDBaseOpenBuildConstants.VERSION);
            // 没有主动设置CallerLocalEntry
            if (TextUtils.isEmpty(request.mLocalEntry)) {
                bundle.putString(DYOpenConstants.Params.CALLER_LOCAL_ENTRY, buildComponentClassName(mContext.getPackageName(), localEntry));
            }
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(remotePackageName, buildComponentClassName(remotePackageName, remoteRequestEntry));
            intent.setComponent(componentName);
            intent.putExtras(bundle);

            // flags
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        return packageName + "." + classPath;
    }
}
