package com.bytedance.sdk.account.open.aweme.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.open.aweme.DYOpenConstants;

/**
 * Powered by WangJiaWei on 2019/1/20.
 */
public abstract class DYBaseResp extends BaseResp {

    /**
     * 错误码
     */
    public int errorCode;

    /**
     * 错误信息
     */
    public String errorMsg;

    /**
     * 扩展信息
     */
    public Bundle extras;

    public DYBaseResp() {

    }

    /**
     * 是否请求成功
     *
     * @return
     */
    public boolean isSuccess() {
        return errorCode == DYOpenConstants.ErrorCode.ERR_OK;
    }

    /**
     * 类型
     *
     * @return
     */
    public abstract int getType();

    /**
     * 判断参数是否符合规范
     *
     * @return
     */
    @SuppressLint("MissingSuperCall")
    @CallSuper
    public boolean checkArgs() {
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    public void toBundle(Bundle bundle) {
        bundle.putInt(DYOpenConstants.Params.ERROR_CODE, errorCode);
        bundle.putString(DYOpenConstants.Params.ERROR_MSG, errorMsg);
        bundle.putInt(DYOpenConstants.Params.TYPE, getType());
        bundle.putBundle(DYOpenConstants.Params.EXTRA, extras);
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    public void fromBundle(Bundle bundle) {
        this.errorCode = bundle.getInt(DYOpenConstants.Params.ERROR_CODE);
        this.errorMsg = bundle.getString(DYOpenConstants.Params.ERROR_MSG);
        this.extras = bundle.getBundle(DYOpenConstants.Params.EXTRA);
    }
}
