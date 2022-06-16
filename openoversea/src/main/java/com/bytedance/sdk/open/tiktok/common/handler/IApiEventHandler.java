package com.bytedance.sdk.open.tiktok.common.handler;
import android.content.Intent;

import com.bytedance.sdk.open.tiktok.common.model.Base;

/**
 * Handle request data and response data
 */
public interface IApiEventHandler {

    /**
     * Handle request data
     *
     * @param req
     */
    void onReq(Base.Request req);

    /**
     * Handle response data
     *
     * @param resp
     */
    void onResp(Base.Response resp);

    /**
     * Handle error intent
     *
     * @param intent
     */
    void onErrorIntent(Intent intent);
}
