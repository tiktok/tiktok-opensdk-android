package com.bytedance.sdk.open.aweme.common.model;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;

/**
 * 基本请求数据
 * Created by yangzhirong on 2018/9/26.
 */
public abstract class BaseReq {

    /**
     * 扩展信息
     */
    public Bundle extras;

    public BaseReq() {

    }

    /**
     * 类型
     *
     * @return
     */
    public abstract int getType();

    @CallSuper
    public boolean checkArgs() {
        return true;
    }

    public String callerPackage;
    public String callerVersion;
    public String callerLocalEntry;

    /**
     * 调用方 entry
     */
    public String getCallerLocalEntry() {
        return callerLocalEntry;
    }

    /**
     * 调用方包名
     *
     * @return
     */
    public String getCallerPackage() {
        return callerPackage;
    }

    /**
     * 调用方版本号
     *
     * @return
     */
    public String getCallerVersion() {
        return callerVersion;
    }

    @CallSuper
    public void toBundle(Bundle bundle) {
        bundle.putInt(BDOpenConstants.Params.TYPE, getType());
        bundle.putBundle(BDOpenConstants.Params.EXTRA, extras);
        bundle.putString(BDOpenConstants.Params.FROM_ENTRY, callerLocalEntry);
    }

    @CallSuper
    public void fromBundle(Bundle bundle) {
        this.callerPackage = bundle.getString(BDOpenConstants.Params.CALLER_PKG);
        this.callerVersion = bundle.getString(BDOpenConstants.Params.CALLER_BASE_OPEN_VERSION);
        this.extras = bundle.getBundle(BDOpenConstants.Params.EXTRA);
        this.callerLocalEntry = bundle.getString(BDOpenConstants.Params.FROM_ENTRY);
    }
}
