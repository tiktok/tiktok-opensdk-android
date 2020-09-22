package com.bytedance.sdk.open.tiktok.common.model;

import android.os.Bundle;

//import com.bytedance.sdk.account.open.aweme.BuildConfig;
import com.bytedance.sdk.open.tiktok.BuildConfig;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;

public abstract class BaseReq {

    /**
     * extra data
     */
    public Bundle extras;

    public BaseReq() {

    }

    /**
     * type
     *
     * @return
     */
    public abstract int getType();

    public boolean checkArgs() {
        return true;
    }

    public String callerPackage;
    public String callerVersion;
    public String callerLocalEntry;

    /**
     * your entry
     */
    public String getCallerLocalEntry() {
        return callerLocalEntry;
    }

    /**
     * your package
     *
     * @return
     */
    public String getCallerPackage() {
        return callerPackage;
    }

    /**
     * your version
     *
     * @return
     */
    public String getCallerVersion() {
        return callerVersion;
    }

    public void toBundle(Bundle bundle) {
        bundle.putInt(ParamKeyConstants.BaseParams.TYPE, getType());
        bundle.putBundle(ParamKeyConstants.BaseParams.EXTRA, extras);
        bundle.putString(ParamKeyConstants.BaseParams.FROM_ENTRY, callerLocalEntry);
        bundle.putString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_SDK_COMMON_NAME, BuildConfig.SDK_OVERSEA_NAME);
        bundle.putString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_SDK_COMMON_VERSION, BuildConfig.SDK_OVERSEA_VERSION);
    }

    public void fromBundle(Bundle bundle) {
        this.callerPackage = bundle.getString(ParamKeyConstants.BaseParams.CALLER_PKG);
        this.callerVersion = bundle.getString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_VERSION);
        this.extras = bundle.getBundle(ParamKeyConstants.BaseParams.EXTRA);
        this.callerLocalEntry = bundle.getString(ParamKeyConstants.BaseParams.FROM_ENTRY);
    }
}
