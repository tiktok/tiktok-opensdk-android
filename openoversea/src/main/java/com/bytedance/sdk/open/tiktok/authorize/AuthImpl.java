package com.bytedance.sdk.open.tiktok.authorize;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.tiktok.utils.AppUtil;

public class AuthImpl {
    private Activity mActivity;
    private String mClientKey;

    public AuthImpl(Activity activity, String clientKey) {
        this.mActivity = activity;
        this.mClientKey = clientKey;
    }


    public boolean authorizeWeb(Class clazz, Authorization.Request req) {
        if (req == null || mActivity == null) {
            return false;
        } else if (!req.checkArgs()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            req.toBundle(bundle);
            bundle.putString(ParamKeyConstants.AuthParams.CLIENT_KEY, mClientKey);
            bundle.putString(ParamKeyConstants.BaseParams.CALLER_PKG, mActivity.getPackageName());
            Intent intent = new Intent(mActivity, clazz);
            intent.putExtras(bundle);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                mActivity.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * new sdk need context to be instance of Activity, so we can use startActivityForResult instead
     * @param req
     * @param packageName
     * @param remoteRequestEntry
     * @param localEntry
     * @param sdkName
     * @param sdkVersion
     * @return
     */
    public boolean authorizeNative(Authorization.Request req, String packageName, String remoteRequestEntry, String localEntry, String sdkName, String sdkVersion) {
        if (TextUtils.isEmpty(packageName) || req == null || mActivity == null) {
            return false;
        } else if (!req.checkArgs()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            req.toBundle(bundle);
            bundle.putString(ParamKeyConstants.AuthParams.CLIENT_KEY, mClientKey);
            bundle.putString(ParamKeyConstants.BaseParams.CALLER_PKG, mActivity.getPackageName());
            if (TextUtils.isEmpty(req.callerLocalEntry)) {
                bundle.putString(ParamKeyConstants.BaseParams.FROM_ENTRY, AppUtil.buildComponentClassName(mActivity.getPackageName(), localEntry));
            }

            bundle.putString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_SDK_NAME, sdkName);
            bundle.putString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_SDK_VERSION, sdkVersion);
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(packageName, AppUtil.buildComponentClassName(packageName, remoteRequestEntry));
            intent.setComponent(componentName);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            try {
                mActivity.startActivityForResult(intent, ParamKeyConstants.AUTH_REQUEST_CODE);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

}
