package com.bytedance.sdk.open.aweme.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.constants.TikTokConstants;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;

/**
 * Powered by WangJiaWei on 2019/1/20.
 */
public abstract class DYBaseResp extends BaseResp {



    public DYBaseResp() {

    }

    /**
     * 是否请求成功
     *
     * @return
     */
    public boolean isSuccess() {
        return errorCode == TikTokConstants.ErrorCode.ERROR_CODE_OK;
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
        bundle.putInt(BDOpenConstants.ShareParams.ERROR_CODE, errorCode);
        bundle.putString(BDOpenConstants.ShareParams.ERROR_MSG, errorMsg);
        bundle.putInt(BDOpenConstants.ShareParams.TYPE, getType());
        bundle.putBundle(BDOpenConstants.ShareParams.EXTRA, extras);
    }

    @SuppressLint("MissingSuperCall")
    @CallSuper
    public void fromBundle(Bundle bundle) {
        this.errorCode = bundle.getInt(BDOpenConstants.ShareParams.ERROR_CODE);
        this.errorMsg = bundle.getString(BDOpenConstants.ShareParams.ERROR_MSG);
        this.extras = bundle.getBundle(BDOpenConstants.ShareParams.EXTRA);
    }
}
