package com.bytedance.sdk.open.douyin.impl;

import android.content.Context;

import com.bytedance.sdk.open.aweme.base.BaseCheckHelperImpl;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;

/**
 * DOUYIN check helper
 */
public class DouYinCheckHelperImpl extends BaseCheckHelperImpl {

    public DouYinCheckHelperImpl(Context context) {
        super(context);
    }

    @Override
    protected int getAuthRequestApi() {
        return ParamKeyConstants.REQUIRED_API_VERSION.AUTH_REQUIRE_API;
    }

    @Override
    public String getSignature() {
        return "aea615ab910015038f73c47e45d21466";
    }

    @Override
    public String getPackageName() {
        return "com.ss.android.ugc.aweme";
    }

    public boolean isSupportShareToContact() {
        return isAppInstalled() && isAppSupportAPI(getPackageName(), "openshare.ShareToContactsActivity", 1);
    }

    public String getRemoteContactActivity() {
        return getPackageName() + "." + "openshare.ShareToContactsActivity";
    }

}
