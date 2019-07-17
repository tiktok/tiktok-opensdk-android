package com.bytedance.sdk.open.aweme.api;
import android.content.Intent;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.share.Share;

/**
 * Tiktok open API
 *
 * @author changlei@bytedance.com
 */
public interface TiktokOpenApi {

    /**
     * send request to authorize. If tiktok is not support authorization, it will get authorization through wap
     *
     * @param request
     * @return
     */
    boolean authorize(Authorization.Request request);


    /**
     * check if the application supports authorization
     * @return
     */
    boolean isAppSupportAuthorization();

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
    boolean handleIntent(Intent intent, TikTokApiEventHandler eventHandler);



}