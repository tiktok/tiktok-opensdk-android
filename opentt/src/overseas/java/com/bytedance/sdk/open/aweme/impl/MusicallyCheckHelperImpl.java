package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;

import com.bytedance.sdk.open.aweme.base.BaseCheckHelperImpl;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;

/**
 * Musically check helper
 * author: ChangLei
 * since: 2019/3/31
 */
public class MusicallyCheckHelperImpl extends BaseCheckHelperImpl {

    public MusicallyCheckHelperImpl(Context context) {
        super(context);
    }

    @Override
    public String getSignature() {
        return "194326e82c84a639a52e5c023116f12a";
    }

    @Override protected int getAuthRequestApi() {
        return ParamKeyConstants.REQUIRED_API_VERSION.AUTH_REQUIRE_API;
    }

    @Override
    public String getPackageName() {
        return "com.zhiliaoapp.musically";
    }
}
