package com.bytedance.sdk.open.aweme;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bytedance.sdk.open.aweme.base.BaseCheckHelperImpl;
import com.bytedance.sdk.open.aweme.common.constants.DYOpenConstants;

/**
 * 抖音检查类
 */
public class AwemeCheckHelperImpl extends BaseCheckHelperImpl {

    public AwemeCheckHelperImpl(Context context) {
        super(context);
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
        return "com.ss.android.ugc.aweme";
    }
}
