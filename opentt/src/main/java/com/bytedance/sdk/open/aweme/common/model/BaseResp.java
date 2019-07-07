package com.bytedance.sdk.open.aweme.common.model;

import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;


/**
 * 基本回调数据
 * Created by yangzhirong on 2018/9/26.
 */

public abstract class BaseResp {

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

    public BaseResp() {

    }

    /**
     * 是否请求取消
     *
     * @return
     */
    public boolean isCancel() {
        return errorCode == BDOpenConstants.ErrorCode.ERROR_CODE_CANCEL;
    }

    /**
     * 是否请求成功
     *
     * @return
     */
    public boolean isSuccess() {
        return errorCode == BDOpenConstants.ErrorCode.ERROR_CODE_OK;
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
    @CallSuper
    public boolean checkArgs() {
        return true;
    }

    @CallSuper
    public void toBundle(Bundle bundle) {
        bundle.putInt(BDOpenConstants.Params.ERROR_CODE, errorCode);
        bundle.putString(BDOpenConstants.Params.ERROR_MSG, errorMsg);
        bundle.putInt(BDOpenConstants.Params.TYPE, getType());
        bundle.putBundle(BDOpenConstants.Params.EXTRA, extras);
    }

    @CallSuper
    public void fromBundle(Bundle bundle) {
        this.errorCode = bundle.getInt(BDOpenConstants.Params.ERROR_CODE);
        this.errorMsg = bundle.getString(BDOpenConstants.Params.ERROR_MSG);
        this.extras = bundle.getBundle(BDOpenConstants.Params.EXTRA);
    }
}
