package com.bytedance.sdk.open.aweme.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;

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
        bundle.putInt(BDOpenConstants.NewVersionParams.TYPE, getType());
        bundle.putBundle(BDOpenConstants.NewVersionParams.EXTRA, extras);
        bundle.putString(BDOpenConstants.NewVersionParams.CALLER_LOCAL_ENTRY, callerLocalEntry);
        bundle.putString(BDOpenConstants.NewVersionParams.CLIENT_KEY, mClientKey);
        bundle.putString(BDOpenConstants.NewVersionParams.CALLER_SDK_VERSION, mCallerSDKVersion);
        bundle.putString(BDOpenConstants.NewVersionParams.CALLER_PKG, mCallerPackage);
        bundle.putString(BDOpenConstants.NewVersionParams.STATE, mState);
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    public void fromBundle(Bundle bundle) {
        this.mCallerPackage = bundle.getString(BDOpenConstants.NewVersionParams.CALLER_PKG);
        this.mCallerSDKVersion = bundle.getString(BDOpenConstants.NewVersionParams.CALLER_SDK_VERSION);
        this.extras = bundle.getBundle(BDOpenConstants.NewVersionParams.EXTRA);
        this.callerLocalEntry = bundle.getString(BDOpenConstants.NewVersionParams.CALLER_LOCAL_ENTRY);
        this.mState = bundle.getString(BDOpenConstants.NewVersionParams.STATE);
        this.mClientKey = bundle.getString(BDOpenConstants.NewVersionParams.CLIENT_KEY);
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