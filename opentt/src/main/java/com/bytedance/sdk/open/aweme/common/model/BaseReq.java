package com.bytedance.sdk.open.aweme.common.model;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;

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
        // TODO: 2019-09-23 在extra字段中添加sdversion
//        extras.putString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_VERSION);

    }

    /**
     * 类型
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

    public void toBundle(Bundle bundle) {
        bundle.putInt(ParamKeyConstants.BaseParams.TYPE, getType());
        bundle.putBundle(ParamKeyConstants.BaseParams.EXTRA, extras);
        bundle.putString(ParamKeyConstants.BaseParams.FROM_ENTRY, callerLocalEntry);
        bundle.putString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_VERSION,ParamKeyConstants.SdkVersion.VERSION);
    }

    public void fromBundle(Bundle bundle) {
        this.callerPackage = bundle.getString(ParamKeyConstants.BaseParams.CALLER_PKG);
        this.callerVersion = bundle.getString(ParamKeyConstants.BaseParams.CALLER_BASE_OPEN_VERSION);
        this.extras = bundle.getBundle(ParamKeyConstants.BaseParams.EXTRA);
        this.callerLocalEntry = bundle.getString(ParamKeyConstants.BaseParams.FROM_ENTRY);
    }
}
