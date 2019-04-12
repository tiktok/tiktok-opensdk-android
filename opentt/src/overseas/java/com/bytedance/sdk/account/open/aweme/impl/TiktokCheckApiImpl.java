package com.bytedance.sdk.account.open.aweme.impl;

import android.support.annotation.NonNull;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;

/**
 * 主要功能：检查Tiktok的相关
 * author: ChangLei
 * since: 2019/4/1
 */
public class TiktokCheckApiImpl extends BaseCheckApiImpl {

    static final int REQUIRE_API = 1; // 用于验证api版本是否支持

    TiktokCheckApiImpl(BDOpenApi bdOpenApi) {
        super(bdOpenApi);
    }

    @Override
    protected int getRequestApi() {
        return REQUIRE_API;
    }

    @NonNull
    @Override
    public String getSignature() {
        return "aea615ab910015038f73c47e45d21466";
    }

    @NonNull
    @Override
    public String getPackageName() {
        return "com.ss.android.ugc.trill";
    }

    @NonNull
    @Override
    public String getRemoteEntryActivity() {
        return "openauthorize.AwemeAuthorizedActivity";
    }
}
