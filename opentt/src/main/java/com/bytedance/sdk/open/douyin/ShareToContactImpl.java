package com.bytedance.sdk.open.douyin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.utils.AppUtil;
import com.bytedance.sdk.open.douyin.constants.ShareContactsMediaConstants;

public class ShareToContactImpl {
    private Activity mActivity;
    private String mClientKey;

    public ShareToContactImpl(Activity activity, String clientKey) {
        this.mActivity = activity;
        this.mClientKey = clientKey;
    }

    public void shareToContacts(String localEntry, String remotePackageName, String remoteRequestEntry, ShareToContact.Request request) {
        if (TextUtils.isEmpty(remotePackageName) || request == null || mActivity == null) {
            return;
        } else if (!request.checkArgs()) {
            return;
        } else {
            Bundle bundle = new Bundle();
            request.toBundle(bundle);
            bundle.putString(ShareContactsMediaConstants.ParamKey.SHARE_CLIENTKEY_KEY, mClientKey);
            if (TextUtils.isEmpty(request.callerLocalEntry)) {
                bundle.putString(ShareContactsMediaConstants.ParamKey.SHARE_CALLER_LOCAL_ENTRY, mActivity.getPackageName() + "." + localEntry);
            }

            if (request.extras != null) {
                bundle.putBundle(ShareContactsMediaConstants.ParamKey.EXTRA, request.extras);
            }

            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(remotePackageName, AppUtil.buildComponentClassName(remotePackageName, remoteRequestEntry));
            intent.setComponent(componentName);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mActivity.startActivityForResult(intent, ParamKeyConstants.SHARE_TO_IM_CODE);
        }
    }
}
