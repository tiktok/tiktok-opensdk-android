package com.bytedance.sdk.open.tiktok.authorize;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.common.constants.Keys;
import com.bytedance.sdk.open.tiktok.utils.AppUtils;

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
            bundle.putString(Keys.Auth.CLIENT_KEY, mClientKey);
            bundle.putString(Keys.Base.CALLER_PKG, mActivity.getPackageName());
            Intent intent = new Intent(mActivity, clazz);
            intent.putExtras(bundle);

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
            bundle.putString(Keys.Auth.CLIENT_KEY, mClientKey);
            bundle.putString(Keys.Base.CALLER_PKG, mActivity.getPackageName());
            if (TextUtils.isEmpty(req.callerLocalEntry)) {
                bundle.putString(Keys.Base.FROM_ENTRY, AppUtils.Companion.componentClassName(mActivity.getPackageName(), localEntry));
            }

            bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, sdkName);
            bundle.putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, sdkVersion);
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(packageName, AppUtils.Companion.componentClassName(packageName, remoteRequestEntry));
            intent.setComponent(componentName);
            intent.putExtras(bundle);

            try {
                mActivity.startActivityForResult(intent, Keys.AUTH_REQUEST_CODE);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }

}
