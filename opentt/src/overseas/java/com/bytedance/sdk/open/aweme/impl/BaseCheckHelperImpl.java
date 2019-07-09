package com.bytedance.sdk.open.aweme.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.model.SendAuth;
import com.bytedance.sdk.open.aweme.common.constants.BDBaseOpenBuildConstants;
import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.constants.DYOpenConstants;
import com.bytedance.sdk.open.aweme.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.common.impl.BDOpenConfig;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.api.BDOpenApi;
import com.bytedance.sdk.open.aweme.utils.AppUtil;
import com.bytedance.sdk.open.aweme.utils.OpenUtils;
import com.bytedance.sdk.open.aweme.utils.SignatureUtils;

/**
 * 主要功能：不同app检查基类
 * author: ChangLei
 * since: 2019/4/1
 */
abstract public class BaseCheckHelperImpl implements IAPPCheckHelper {

//    private final BDOpenApi mBdOpenApi;
    private Context mContext;
//    private BDOpenConfig openConfig;

    BaseCheckHelperImpl(Context context) {
//        this.mBdOpenApi = bdOpenApi;
        this.mContext = context;
//        this.openConfig = config;

    }

    @Override
    public boolean isAppSupportAuthorization() {
        return isAppInstalled()
                && isAppSupportAuthApi()
                && SignatureUtils.validateSign(mContext, getPackageName(), getSignature());
    }

    @Override
    public boolean isAppSupportShare() {
        return isAppInstalled() && isAppSupportShareApi();
    }



    private boolean isAppSupportAPI() {
        if (mContext == null || TextUtils.isEmpty(getPackageName())) {
            return false;
        }

        if (!AppUtil.isAppInstalled(mContext, getPackageName())) {
            return false;
        }

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(getPackageName(), buildComponentClassName(getPackageName(), getRemoteAuthEntryActivity()));
        intent.setComponent(componentName);
        ActivityInfo activityInfo = intent.resolveActivityInfo(mContext.getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY);
        int platformSdkVersion = getPlatformSDKVersion(getPackageName(), getRemoteAuthEntryActivity());
        return activityInfo != null && activityInfo.exported && (platformSdkVersion >= getAuthRequestApi());
    }

    private boolean isAppSupportAuthApi() {
        return isAppSupportAPI();
    }


    private String buildComponentClassName(String packageName, String classPath) {
        return packageName + "." + classPath;
    }


    public int getPlatformSDKVersion(String platformPackageName, String remoteRequestEntry) {
        if (mContext == null || TextUtils.isEmpty(platformPackageName)) {
            return BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR;
        }
        if (!AppUtil.isAppInstalled(mContext, getPackageName())) {
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

    private boolean isAppSupportShareApi() {
        return isAppSupportAPI();
    }

    public boolean isAppInstalled() {
        return AppUtil.isAppInstalled(mContext, getPackageName());
    }

//    @Override
//    public boolean sendRemoteRequest(String localEntryActivity, BaseReq req) {
//
//        if (TextUtils.isEmpty(getPackageName()) || req == null || mContext == null) {
//            return false;
//        } else if (!req.checkArgs()) {
//            return false;
//        } else {
//            // 兼容以前版本，把可选权限添加到scope字段
//            if (req instanceof SendAuth.Request) {
//                OpenUtils.handleRequestScope((SendAuth.Request)req);
//            }
//            Bundle bundle = new Bundle();
//            req.toBundle(bundle);
//            bundle.putString(BDOpenConstants.Params.CLIENT_KEY, openConfig.clientKey);
//            bundle.putString(BDOpenConstants.Params.CALLER_PKG, mContext.getPackageName());
//            bundle.putString(BDOpenConstants.Params.CALLER_BASE_OPEN_VERSION, BDBaseOpenBuildConstants.VERSION);
//            // 没有主动设置CallerLocalEntry
//            if (TextUtils.isEmpty(req.callerLocalEntry)) {
//                bundle.putString(BDOpenConstants.Params.FROM_ENTRY, buildComponentClassName(mContext.getPackageName(), localEntryActivity));
//            }
//            Intent intent = new Intent();
//            ComponentName componentName = new ComponentName(getPackageName(), buildComponentClassName(getPackageName(), getRemoteAuthEntryActivity()));
//            intent.setComponent(componentName);
//            intent.putExtras(bundle);
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            }
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            }
//            try {
//                mContext.startActivity(intent);
//                return true;
//            } catch (Exception e) {
//                return false;
//            }
//        }
//
//    }

    @NonNull
    @Override
    public String getRemoteAuthEntryActivity() {
        return "openauthorize.AwemeAuthorizedActivity";
    }

    /**
     * M T有差异 (可能)
     * @return
     */
    abstract protected int getAuthRequestApi();

    /**
     * M T有差异
     * @return
     */
    public abstract String getSignature();
}
