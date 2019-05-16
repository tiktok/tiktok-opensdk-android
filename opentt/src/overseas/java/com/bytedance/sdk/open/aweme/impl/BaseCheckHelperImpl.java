package com.bytedance.sdk.open.aweme.impl;

import android.support.annotation.NonNull;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.DYOpenConstants;
import com.bytedance.sdk.open.aweme.IAPPCheckHelper;

/**
 * 主要功能：不同app检查基类
 * author: ChangLei
 * since: 2019/4/1
 */
abstract public class BaseCheckHelperImpl implements IAPPCheckHelper {

    private final BDOpenApi mBdOpenApi;

    BaseCheckHelperImpl(BDOpenApi bdOpenApi) {
        mBdOpenApi = bdOpenApi;
    }

    @Override
    public boolean isAppSupportAuthorization() {
        return isAppInstalled()
                && isAppSupportAuthApi()
                && mBdOpenApi.validateSign(getPackageName(), getSignature());
    }

    @Override
    public boolean isAppSupportShare() {
        return isAppInstalled() && isAppSupportShareApi();
    }

    private boolean isAppSupportAuthApi() {
        return mBdOpenApi.isAppSupportAPI(getPackageName(), getRemoteAuthEntryActivity(), getAuthRequestApi());
    }

    private boolean isAppSupportShareApi() {
        return mBdOpenApi.isAppSupportAPI(getPackageName(), getRemoteAuthEntryActivity(), DYOpenConstants.REQUIRED_API_VERSION.SHARE_REQUIRED_MIN_VERSION);
    }

    private boolean isAppInstalled() {
        return mBdOpenApi.isAppInstalled(getPackageName());
    }

    @Override
    public boolean sendRemoteRequest(String localEntryActivity, BaseReq req) {
        return mBdOpenApi.sendRemoteRequest(localEntryActivity, getPackageName(), getRemoteAuthEntryActivity(), req);
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
