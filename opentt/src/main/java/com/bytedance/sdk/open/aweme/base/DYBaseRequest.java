package com.bytedance.sdk.open.aweme.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.DYOpenConstants;

public abstract class DYBaseRequest extends BaseReq {

    public String mCallerPackage;
    public String mCallerSDKVersion;

    public String mClientKey;

    public String mState;

    /**
     * 扩展信息
     */
    public Bundle extras;

    /**
     * 类型
     *
     * @return
     */
    public abstract int getType();

    @SuppressLint("MissingSuperCall")
    @CallSuper
    public void toBundle(Bundle bundle) {
        bundle.putInt(DYOpenConstants.Params.TYPE, getType());
        bundle.putBundle(DYOpenConstants.Params.EXTRA, extras);
        bundle.putString(DYOpenConstants.Params.CALLER_LOCAL_ENTRY, callerLocalEntry);
        bundle.putString(DYOpenConstants.Params.CLIENT_KEY, mClientKey);
        bundle.putString(DYOpenConstants.Params.CALLER_SDK_VERSION, mCallerSDKVersion);
        bundle.putString(DYOpenConstants.Params.CALLER_PKG, mCallerPackage);
        bundle.putString(DYOpenConstants.Params.STATE, mState);
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    public void fromBundle(Bundle bundle) {
        this.mCallerPackage = bundle.getString(DYOpenConstants.Params.CALLER_PKG);
        this.mCallerSDKVersion = bundle.getString(DYOpenConstants.Params.CALLER_SDK_VERSION);
        this.extras = bundle.getBundle(DYOpenConstants.Params.EXTRA);
        this.callerLocalEntry = bundle.getString(DYOpenConstants.Params.CALLER_LOCAL_ENTRY);
        this.mState = bundle.getString(DYOpenConstants.Params.STATE);
        this.mClientKey = bundle.getString(DYOpenConstants.Params.CLIENT_KEY);
    }

    @Override
    public String getCallerLocalEntry() {
        return callerLocalEntry;
    }

    @Override
    public String getCallerPackage() {
        return mCallerPackage;
    }

    @Override
    public String getCallerVersion() {
        return mCallerSDKVersion;
    }

    public interface ErrCode {
        int ERR_OK = 0;
        int ERR_FAILED = -1;
        int ERR_USER_CANCEL = -2;
        int ERR_UNSUPPORT = -3;
    }
}