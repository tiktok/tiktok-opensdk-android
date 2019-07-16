package com.bytedance.sdk.open.aweme.api;
import android.content.Intent;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.share.Share;

/**
 * Tiktok open API
 *
 * @author changlei@bytedance.com
 */
public interface TiktokOpenApi {

    /**
     * parse response or request data in intent
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    boolean handleIntent(Intent intent, TikTokApiEventHandler eventHandler);


    /**
     * check if the application supports authorization
     * @return
     */
    boolean isAppSupportAuthorization();

    /**
     *
     * check if the application supports sharing
     * @return
     */
    boolean isAppSupportShare();

    /**
     * send request to authorize. If tiktok is not support authorization, it will get authorization through wap
     *
     * @param request
     * @return
     */
    boolean authorize(Authorization.Request request);

    /**
     * share image/video
     *
     * @return
     */
    boolean share(Share.Request request);


}