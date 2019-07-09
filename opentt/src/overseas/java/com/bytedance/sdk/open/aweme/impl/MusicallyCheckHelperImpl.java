package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bytedance.sdk.open.aweme.common.constants.DYOpenConstants;

/**
 * 主要功能：检查Musically的相关
 * author: ChangLei
 * since: 2019/3/31
 */
public class MusicallyCheckHelperImpl extends BaseCheckHelperImpl {

    MusicallyCheckHelperImpl(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public String getSignature() {
        return "194326e82c84a639a52e5c023116f12a";
    }

    @Override protected int getAuthRequestApi() {
        return DYOpenConstants.REQUIRED_API_VERSION.AUTH_REQUIRE_API;
    }

    @NonNull
    @Override
    public String getPackageName() {
        return "com.zhiliaoapp.musically";
    }
}
