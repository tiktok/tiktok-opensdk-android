package com.bytedance.sdk.open.aweme.common.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bytedance.sdk.open.aweme.base.BaseCheckHelperImpl;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;

/**
 * 主要功能：检查Musically的相关
 * author: ChangLei
 * since: 2019/3/31
 */
public class MusicallyCheckHelperImpl extends BaseCheckHelperImpl {

    public MusicallyCheckHelperImpl(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public String getSignature() {
        return "194326e82c84a639a52e5c023116f12a";
    }

    @Override protected int getAuthRequestApi() {
        return ParamKeyConstants.REQUIRED_API_VERSION.AUTH_REQUIRE_API;
    }

    @NonNull
    @Override
    public String getPackageName() {
        return "com.zhiliaoapp.musically";
    }
}
