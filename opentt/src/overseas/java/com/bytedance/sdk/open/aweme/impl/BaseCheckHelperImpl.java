package com.bytedance.sdk.open.aweme.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.constants.DYOpenConstants;
import com.bytedance.sdk.open.aweme.utils.AppUtil;
import com.bytedance.sdk.open.aweme.utils.SignatureUtils;

/**
 * 主要功能：不同app检查基类
 * author: ChangLei
 * since: 2019/4/1
 */
abstract public class BaseCheckHelperImpl implements IAPPCheckHelper {

    private Context mContext;

    BaseCheckHelperImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean isAppSupportAuthorization() {
        return isAppInstalled()
                && isAppSupportAuthApi()
                && SignatureUtils.validateSign(mContext, getPackageName(), getSignature());
    }

    @Override
    public boolean isAppSupportShare() {
        return isAppInstalled() && isAppSupportShareApi(getPackageName(), getRemoteAuthEntryActivity(), DYOpenConstants.REQUIRED_API_VERSION.SHARE_REQUIRED_MIN_VERSION);
    }


    private boolean isAppSupportAPI(String platformPackageName, String remoteRequestEntry, int requiredApi) {
        if (mContext == null || TextUtils.isEmpty(platformPackageName)) {
            return false;
        }

        if (!isAppInstalled()) {
            return false;
        }

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(platformPackageName, AppUtil.buildComponentClassName(platformPackageName, remoteRequestEntry));
        intent.setComponent(componentName);
        ActivityInfo activityInfo = intent.resolveActivityInfo(mContext.getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY);
        int platformSdkVersion = getPlatformSDKVersion(platformPackageName, remoteRequestEntry);
        return activityInfo != null && activityInfo.exported && (platformSdkVersion >= requiredApi);
    }

    private boolean isAppSupportAuthApi() {
        return isAppSupportAPI(getPackageName(), getRemoteAuthEntryActivity(), getAuthRequestApi());
    }



    public int getPlatformSDKVersion(String platformPackageName, String remoteRequestEntry) {
        if (mContext == null || TextUtils.isEmpty(platformPackageName)) {
            return BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR;
        }
        if (!AppUtil.isAppInstalled(mContext, getPackageName())) {
            return BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR;
        }
        try {
            ComponentName componentName = new ComponentName(platformPackageName, AppUtil.buildComponentClassName(platformPackageName, remoteRequestEntry));
            ActivityInfo appInfo = mContext.getPackageManager().getActivityInfo(componentName, PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                return appInfo.metaData.getInt(BDOpenConstants.META_PLATFORM_SDK_VERSION, BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR;
    }

    private boolean isAppSupportShareApi(String platformPackageName, String remoteRequestEntry, int requiredApi) {
        return isAppSupportAPI(platformPackageName, remoteRequestEntry, requiredApi);
    }

    public boolean isAppInstalled() {
        return AppUtil.isAppInstalled(mContext, getPackageName());
    }

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
