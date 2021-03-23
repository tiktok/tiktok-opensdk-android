package com.bytedance.sdk.open.tiktok.helper;

import android.content.Context;

import com.bytedance.sdk.open.tiktok.base.BaseCheckHelperImpl;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;

public class TikTokCheckHelperImpl extends BaseCheckHelperImpl {

    public TikTokCheckHelperImpl(Context context) {
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
    public boolean isShareSupportFileProvider() {
        return  isAppSupportAPI(getPackageName(), getRemoteAuthEntryActivity(),
                ParamKeyConstants.REQUIRED_API_VERSION.SHARE_SUPPORT_FILEPROVIDER);
    }

    @Override
    public String getPackageName() {
        return "com.ss.android.ugc.trill";
    }
}
