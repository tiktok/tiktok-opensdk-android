package com.bytedance.sdk.open.aweme.authorize;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.constants.BDBaseOpenBuildConstants;
import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.impl.BDOpenConfig;
import com.bytedance.sdk.open.aweme.utils.AppUtil;
import com.bytedance.sdk.open.aweme.utils.OpenUtils;

public class AuthImpl {
    private Context mContext;
    private BDOpenConfig openConfig;

    public AuthImpl(Context context, BDOpenConfig sConfig) {
        this.mContext = context;
        this.openConfig = sConfig;
    }

    public boolean authorizeWeb(Class clazz, Authorization.Request req) {
        if (req == null || mContext == null) {
            return false;
        } else if (!req.checkArgs()) {
            return false;
        } else {
            // 兼容以前版本，把可选权限添加到scope字段
            OpenUtils.handleRequestScope(req);
            Bundle bundle = new Bundle();
            req.toBundle(bundle);
            bundle.putString(BDOpenConstants.Params.CLIENT_KEY, openConfig.clientKey);
            bundle.putString(BDOpenConstants.Params.CALLER_PKG, mContext.getPackageName());
            bundle.putString(BDOpenConstants.Params.CALLER_BASE_OPEN_VERSION, BDBaseOpenBuildConstants.VERSION);
            Intent intent = new Intent(mContext, clazz);
            intent.putExtras(bundle);
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



    public boolean authorizeNative(Authorization.Request req, String packageName, String remoteRequestEntry, String localEntry) {
        if (TextUtils.isEmpty(packageName) || req == null || mContext == null) {
            return false;
        } else if (!req.checkArgs()) {
            return false;
        } else {
            // 兼容以前版本，把可选权限添加到scope字段
            if (req instanceof Authorization.Request) {
                OpenUtils.handleRequestScope(req);
            }
            Bundle bundle = new Bundle();
            req.toBundle(bundle);
            bundle.putString(BDOpenConstants.Params.CLIENT_KEY, openConfig.clientKey);
            bundle.putString(BDOpenConstants.Params.CALLER_PKG, mContext.getPackageName());
            bundle.putString(BDOpenConstants.Params.CALLER_BASE_OPEN_VERSION, BDBaseOpenBuildConstants.VERSION);
            // 没有主动设置CallerLocalEntry
            if (TextUtils.isEmpty(req.callerLocalEntry)) {
                bundle.putString(BDOpenConstants.Params.FROM_ENTRY, AppUtil.buildComponentClassName(mContext.getPackageName(), localEntry));
            }
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(packageName, AppUtil.buildComponentClassName(packageName, remoteRequestEntry));
            intent.setComponent(componentName);
            intent.putExtras(bundle);

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

}
