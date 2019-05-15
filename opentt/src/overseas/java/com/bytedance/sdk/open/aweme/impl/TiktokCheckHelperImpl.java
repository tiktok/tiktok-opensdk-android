package com.bytedance.sdk.open.aweme.impl;

import android.support.annotation.NonNull;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;
import com.bytedance.sdk.open.aweme.DYOpenConstants;

/**
 * 主要功能：检查Tiktok的相关
 * author: ChangLei
 * since: 2019/4/1
 */
public class TiktokCheckHelperImpl extends BaseCheckHelperImpl {

    TiktokCheckHelperImpl(BDOpenApi bdOpenApi) {
        super(bdOpenApi);
    }

    @Override
    protected int getAuthRequestApi() {
        return DYOpenConstants.REQUIRED_API_VERSION.AUTH_REQUIRE_API;
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
}
