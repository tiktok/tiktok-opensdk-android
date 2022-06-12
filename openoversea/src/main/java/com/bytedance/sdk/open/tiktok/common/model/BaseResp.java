package com.bytedance.sdk.open.tiktok.common.model;

import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.common.constants.Constants;
import com.bytedance.sdk.open.tiktok.common.constants.Keys;



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
        return errorCode == Constants.BaseError.ERROR_CANCEL;
    }

    /**
     * if request is success
     *
     * @return
     */
    public boolean isSuccess() {
        return errorCode == Constants.BaseError.OK;
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
        bundle.putInt(Keys.Base.ERROR_CODE, errorCode);
        bundle.putString(Keys.Base.ERROR_MSG, errorMsg);
        bundle.putInt(Keys.Base.TYPE, getType());
        bundle.putBundle(Keys.Base.EXTRA, extras);
    }

    public void fromBundle(Bundle bundle) {
        this.errorCode = bundle.getInt(Keys.Base.ERROR_CODE);
        this.errorMsg = bundle.getString(Keys.Base.ERROR_MSG);
        this.extras = bundle.getBundle(Keys.Base.EXTRA);
    }
}
