package com.bytedance.sdk.open.tiktok.common.handler;
import android.content.Intent;

import com.bytedance.sdk.open.tiktok.common.model.BaseReq;
import com.bytedance.sdk.open.tiktok.common.model.BaseResp;

/**
 * Handle request data and response data
 */
public interface IApiEventHandler {

    /**
     * Handle request data
     *
     * @param req
     */
    void onReq(BaseReq req);

    /**
     * Handle response data
     *
     * @param resp
     */
    void onResp(BaseResp resp);

    /**
     * Handle error intent
     *
     * @param intent
     */
    void onErrorIntent(Intent intent);
}
