package com.bytedance.sdk.open.tiktok.api;

import android.content.Intent;

import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.share.Share;
import com.bytedance.sdk.open.tiktok.share.ShareRequest;

public interface TikTokOpenApi {


    /**
     * share image/video
     *
     * @return
     */
    boolean share(Share.Request request);

    /**
     * share image/video
     *
     * @return
     */
    boolean share(ShareRequest request);

    /**
     *
     * check if the application supports sharing
     * @return
     */
    boolean isAppSupportShare();

    /**
     *
     * check if the version enables FileProvider
     * @return
     */
    boolean isShareSupportFileProvider();

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
