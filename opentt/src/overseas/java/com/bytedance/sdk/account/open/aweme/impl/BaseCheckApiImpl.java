package com.bytedance.sdk.account.open.aweme.impl;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.open.aweme.api.TiktokCheckApi;

/**
 * 主要功能：不同app检查基类
 * author: ChangLei
 * since: 2019/4/1
 */
abstract public class BaseCheckApiImpl implements TiktokCheckApi {

    private final BDOpenApi mBdOpenApi;
    BaseCheckApiImpl(BDOpenApi bdOpenApi) {
        mBdOpenApi = bdOpenApi;
    }

    @Override
    public boolean couldAppBeUsedForAuthorization() {
        return isAppInstalled()
                && isAppSupportAPI()
                && mBdOpenApi.validateSign(getPackageName(), getSignature());
    }

    @Override
    public boolean isAppSupportAPI() {
        return mBdOpenApi.isAppSupportAPI(getPackageName(), getRemoteEntryActivity(), getRequestApi());
    }

    @Override
    public boolean isAppInstalled() {
        return mBdOpenApi.isAppInstalled(getPackageName());
    }

    @Override
    public boolean sendRemoteRequest(String localEntryActivity, BaseReq req) {
        return mBdOpenApi.sendRemoteRequest(localEntryActivity, getPackageName(), getRemoteEntryActivity(), req);
    }

    abstract protected int getRequestApi();
}
