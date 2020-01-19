package com.bytedance.sdk.open.aweme.authorize;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.utils.AppUtil;

/**
 *
 * @author cassie.wang@bytedance.com
 */

public class AuthImpl {
    private Context mContext;
    private String mClientKey;

    public AuthImpl(Context context, String clientKey) {
        this.mContext = context;
        this.mClientKey = clientKey;
    }


    public boolean authorizeWeb(Class clazz, Authorization.Request req) {
        if (req == null || mContext == null) {
            return false;
        } else if (!req.checkArgs()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            req.toBundle(bundle);
            bundle.putString(ParamKeyConstants.AuthParams.CLIENT_KEY, mClientKey);
            bundle.putString(ParamKeyConstants.BaseParams.CALLER_PKG, mContext.getPackageName());
            Intent intent = new Intent(mContext, clazz);
            intent.putExtras(bundle);

            if (mContext instanceof Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            try {
                mContext.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }


    public boolean authorizeNative(Authorization.Request req, String packageName, String remoteRequestEntry, String localEntry) {
        if (TextUtils.isEmpty(packageName) || req == null || mContext == null) {
            return false;
        } else if (!req.checkArgs()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            req.toBundle(bundle);
            bundle.putString(ParamKeyConstants.AuthParams.CLIENT_KEY, mClientKey);
            bundle.putString(ParamKeyConstants.BaseParams.CALLER_PKG, mContext.getPackageName());
            if (TextUtils.isEmpty(req.callerLocalEntry)) {
                bundle.putString(ParamKeyConstants.BaseParams.FROM_ENTRY, AppUtil.buildComponentClassName(mContext.getPackageName(), localEntry));
            }
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(packageName, AppUtil.buildComponentClassName(packageName, remoteRequestEntry));
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

}
