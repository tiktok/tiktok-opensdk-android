package com.bytedance.sdk.open.tiktok.common.model;

import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.CommonConstants;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;



public abstract class BaseResp {

    /**
     * error code
     */
    public int errorCode;

    /**
     * error message
     */
    public String errorMsg;

    /**
     * extra data
     */
    public Bundle extras;

    public BaseResp() {

    }

    /**
     *
     * @return
     */
    public boolean isCancel() {
        return errorCode == CommonConstants.BaseErrorCode.ERROR_CANCEL;
    }

    /**
     * if request is success
     *
     * @return
     */
    public boolean isSuccess() {
        return errorCode == CommonConstants.BaseErrorCode.OK;
    }

    /**
     * authorization of sharing
     *
     * @return
     */
    public abstract int getType();

    /**
     *
     * @return
     */
    public boolean checkArgs() {
        return true;
    }

    public void toBundle(Bundle bundle) {
        bundle.putInt(ParamKeyConstants.BaseParams.ERROR_CODE, errorCode);
        bundle.putString(ParamKeyConstants.BaseParams.ERROR_MSG, errorMsg);
        bundle.putInt(ParamKeyConstants.BaseParams.TYPE, getType());
        bundle.putBundle(ParamKeyConstants.BaseParams.EXTRA, extras);
    }

    public void fromBundle(Bundle bundle) {
        this.errorCode = bundle.getInt(ParamKeyConstants.BaseParams.ERROR_CODE);
        this.errorMsg = bundle.getString(ParamKeyConstants.BaseParams.ERROR_MSG);
        this.extras = bundle.getBundle(ParamKeyConstants.BaseParams.EXTRA);
    }
}
