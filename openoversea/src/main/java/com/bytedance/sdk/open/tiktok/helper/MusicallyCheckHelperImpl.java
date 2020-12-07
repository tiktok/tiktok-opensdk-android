package com.bytedance.sdk.open.tiktok.helper;

import android.content.Context;

import com.bytedance.sdk.open.tiktok.base.BaseCheckHelperImpl;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;

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
    public boolean isShareSupportFileProvider() {
        return  isAppSupportAPI(getPackageName(), getRemoteAuthEntryActivity(),
                ParamKeyConstants.REQUIRED_API_VERSION.SHARE_SUPPORT_FILEPROVIDER);
    }

    @Override
    public String getPackageName() {
        return "com.zhiliaoapp.musically";
    }
}
