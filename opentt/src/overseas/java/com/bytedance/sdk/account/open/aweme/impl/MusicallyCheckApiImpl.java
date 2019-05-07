package com.bytedance.sdk.account.open.aweme.impl;

import android.support.annotation.NonNull;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;

/**
 * 主要功能：检查Musically的相关
 * author: ChangLei
 * since: 2019/3/31
 */
public class MusicallyCheckApiImpl extends BaseCheckApiImpl {

    static final int REQUIRE_API = 1; // 用于验证api版本是否支持

    MusicallyCheckApiImpl(BDOpenApi bdOpenApi) {
        super(bdOpenApi);
    }

    @Override
    protected int getRequestApi() {
        return REQUIRE_API;
    }

    @NonNull
    @Override
    public String getSignature() {
        return "194326e82c84a639a52e5c023116f12a";
    }

    @NonNull
    @Override
    public String getPackageName() {
        return "com.zhiliaoapp.musically";
    }

    @NonNull
    @Override
    public String getRemoteEntryActivity() {
        return "openauthorize.AwemeAuthorizedActivity";
    }

}
