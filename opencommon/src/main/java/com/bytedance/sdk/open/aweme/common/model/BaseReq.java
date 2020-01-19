package com.bytedance.sdk.open.aweme.common.model;

import android.os.Bundle;

//import com.bytedance.sdk.account.open.aweme.BuildConfig;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;

/**
 * Base Model
 * Created by yangzhirong on 2018/9/26.
 */
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
//        bundle.putString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_VERSION, BuildConfig.SDK_NAME + " " + BuildConfig.SDK_VERSION);
    }

    public void fromBundle(Bundle bundle) {
        this.callerPackage = bundle.getString(ParamKeyConstants.BaseParams.CALLER_PKG);
        this.callerVersion = bundle.getString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_VERSION);
        this.extras = bundle.getBundle(ParamKeyConstants.BaseParams.EXTRA);
        this.callerLocalEntry = bundle.getString(ParamKeyConstants.BaseParams.FROM_ENTRY);
    }
}
