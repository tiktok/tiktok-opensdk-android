package com.bytedance.sdk.account.open.aweme.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.account.bdopen.api.BDBaseOpenBuildConstants;
import com.bytedance.sdk.account.bdopen.impl.BDOpenConfig;
import com.bytedance.sdk.account.common.constants.BDOpenConstants;

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
            bundle.putString(BDOpenConstants.Params.CLIENT_KEY, openConfig.clientKey);
            bundle.putString(BDOpenConstants.Params.CALLER_PKG, mContext.getPackageName());
            bundle.putString(BDOpenConstants.Params.CALLER_BASE_OPEN_VERSION, BDBaseOpenBuildConstants.VERSION);
            // 没有主动设置CallerLocalEntry
            if (TextUtils.isEmpty(request.callerLocalEntry)) {
                bundle.putString(BDOpenConstants.Params.FROM_ENTRY, buildComponentClassName(mContext.getPackageName(), localEntry));
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

            //medias
            if (request.medias.size() == 1) {
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, request.medias.get(0));
            } else  {
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, request.medias);
            }
            if (request.shareType == Share.VIDEO) {
                intent.setType("video/*");
            } else if (request.shareType == Share.IMAGE) {
                intent.setType("image/*");
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
