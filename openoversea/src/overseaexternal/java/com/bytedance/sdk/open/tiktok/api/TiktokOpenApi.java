package com.bytedance.sdk.open.tiktok.api;

import android.content.Intent;

import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.share.Share;

public interface TiktokOpenApi {


    /**
     * share image/video
     *
     * @return
     */
    boolean share(Share.Request request);

    /**
     *
     * check if the application supports sharing
     * @return
     */
    boolean isAppSupportShare();

    /**
     * parse response or request data in intent
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    boolean handleIntent(Intent intent, IApiEventHandler eventHandler);

    boolean isAppInstalled();



}
