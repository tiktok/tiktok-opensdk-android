package com.bytedance.sdk.open.aweme.common.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;


import com.bytedance.sdk.open.aweme.authorize.WebViewHelper;
import com.bytedance.sdk.open.aweme.authorize.handler.SendAuthDataHandler;
import com.bytedance.sdk.open.aweme.common.handler.BDApiEventHandler;
import com.bytedance.sdk.open.aweme.common.handler.BDDataHandler;
import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.authorize.model.SendAuth;
import com.bytedance.sdk.open.aweme.utils.AppUtil;
import com.bytedance.sdk.open.aweme.utils.OpenUtils;
import com.bytedance.sdk.open.aweme.utils.SignatureUtils;
import com.bytedance.sdk.open.aweme.common.constants.BDBaseOpenBuildConstants;
import com.bytedance.sdk.open.aweme.common.api.BDOpenApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhirong on 2018/10/8.
 */
class BDOpenApiImpl implements BDOpenApi {

    private final static int FLAGS_GET_ONLY_FROM_ANDROID = 0x01000000;

    private Context mContext;

    private List<BDDataHandler> handlers = new ArrayList<>();

    private BDOpenConfig openConfig;

    public BDOpenApiImpl(Context context, BDOpenConfig config, List<BDDataHandler> handlers) {
        this.mContext = context;
        this.openConfig = config;
        this.handlers.add(new SendAuthDataHandler());
        if (handlers != null) {
            this.handlers.addAll(handlers);
        }
    }

    @Override
    public boolean handleIntent(Intent intent, BDApiEventHandler eventHandler) {
        if (eventHandler == null) {
            return false;
        }
        if (intent == null) {
            eventHandler.onErrorIntent(intent);
            return false;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            eventHandler.onErrorIntent(intent);
            return false;
        }
        int type = bundle.getInt(BDOpenConstants.Params.TYPE);
        for (BDDataHandler handler : handlers) {
            if (handler.handle(type, bundle, eventHandler)) {
                return true;
            }
        }
        eventHandler.onErrorIntent(intent);
        return false;
    }

    @Override
    public boolean isAppInstalled(String platformPackageName) {
        return AppUtil.isAppInstalled(mContext, platformPackageName);
    }

    @Override
    public boolean isAppSupportAPI(String platformPackageName, String remoteRequestEntry, int requiredApi) {
        if (mContext == null || TextUtils.isEmpty(platformPackageName)) {
            return false;
        }

        if (!isAppInstalled(platformPackageName)) {
            return false;
        }

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(platformPackageName, buildComponentClassName(platformPackageName, remoteRequestEntry));
        intent.setComponent(componentName);
        ActivityInfo activityInfo = intent.resolveActivityInfo(mContext.getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY);
        int platformSdkVersion = getPlatformSDKVersion(platformPackageName, remoteRequestEntry);
        return activityInfo != null && activityInfo.exported && (platformSdkVersion >= requiredApi);
    }

    private String buildComponentClassName(String packageName, String classPath) {
        return packageName + "." + classPath;
    }

    @Override
    public int getPlatformSDKVersion(String platformPackageName, String remoteRequestEntry) {
        if (mContext == null || TextUtils.isEmpty(platformPackageName)) {
            return BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR;
        }
        if (!isAppInstalled(platformPackageName)) {
            return BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR;
        }
        try {
            ComponentName componentName = new ComponentName(platformPackageName, buildComponentClassName(platformPackageName, remoteRequestEntry));
            ActivityInfo appInfo = mContext.getPackageManager().getActivityInfo(componentName, PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                return appInfo.metaData.getInt(BDOpenConstants.META_PLATFORM_SDK_VERSION, BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR;
    }

    @Override
    public boolean openApp(String platformPackageName) {
        return AppUtil.openApp(mContext, platformPackageName);
    }

    @Override
    public boolean sendInnerResponse(String localEntry, SendAuth.Request req, BaseResp resp) {
        if (resp == null || mContext == null) {
            return false;
        } else if (!resp.checkArgs()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            resp.toBundle(bundle);
            String platformPackageName = mContext.getPackageName();
            String localResponseEntry = TextUtils.isEmpty(req.callerLocalEntry) ? buildComponentClassName(platformPackageName, localEntry) : req.callerLocalEntry;
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(platformPackageName, localResponseEntry);
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

    @Override
    public boolean sendRemoteRequest(String localEntry, String remotePackageName, String remoteRequestEntry, BaseReq req) {
        if (TextUtils.isEmpty(remotePackageName) || req == null || mContext == null) {
            return false;
        } else if (!req.checkArgs()) {
            return false;
        } else {
            // 兼容以前版本，把可选权限添加到scope字段
            if (req instanceof SendAuth.Request) {
                OpenUtils.handleRequestScope((SendAuth.Request)req);
            }
            Bundle bundle = new Bundle();
            req.toBundle(bundle);
            bundle.putString(BDOpenConstants.Params.CLIENT_KEY, openConfig.clientKey);
            bundle.putString(BDOpenConstants.Params.CALLER_PKG, mContext.getPackageName());
            bundle.putString(BDOpenConstants.Params.CALLER_BASE_OPEN_VERSION, BDBaseOpenBuildConstants.VERSION);
            // 没有主动设置CallerLocalEntry
            if (TextUtils.isEmpty(req.callerLocalEntry)) {
                bundle.putString(BDOpenConstants.Params.FROM_ENTRY, buildComponentClassName(mContext.getPackageName(), localEntry));
            }
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(remotePackageName, buildComponentClassName(remotePackageName, remoteRequestEntry));
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

    @Override
    public boolean sendInnerWebAuthRequest(Class clazz, SendAuth.Request req) {
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

    @Override
    public boolean validateSign(String pkgName, String sign) {
        return SignatureUtils.validateSign(mContext, pkgName, sign);
    }

    @Override
    public boolean preloadWebAuth(SendAuth.Request req, String host, String path, String domain) {
        if (req == null || mContext == null) {
            return false;
        } else if (!req.checkArgs()) {
            return false;
        } else {
            req.clientKey = openConfig.clientKey;
            req.callerPackage = mContext.getPackageName();
            req.callerVersion = BDBaseOpenBuildConstants.VERSION;
            req.redirectUri = "https://" + domain + BDOpenConstants.REDIRECT_URL_PATH;
            // 兼容以前版本，把可选权限添加到scope字段
            OpenUtils.handleRequestScope(req);
            WebViewHelper.preload(mContext, req, host, path);
            return true;
        }
    }
}
