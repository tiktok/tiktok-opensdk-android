package com.bytedance.sdk.open.aweme.api;
import android.content.Intent;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.open.aweme.authorize.Authorization;
import com.bytedance.sdk.open.aweme.share.Share;

/**
 * Tiktok open API
 *
 * @author changlei@bytedance.com
 */
public interface TiktokOpenApi {

    /**
     * parse Intent request
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    boolean handleIntent(Intent intent, BDApiEventHandler eventHandler);

    boolean isAppSupportAuthorization(int targetApp);

    /**
     *
     * @param targetApp tiktok、tiktok-m、抖音
     * @return
     */
    boolean isAppSupportShare(int targetApp);

    boolean sendInnerResponse(SendAuth.Request req, BaseResp resp);

    /**
     * to authorize from wap
     *
     * @param request
     * @return
     */
    boolean sendInnerWebAuthRequest(Authorization.Request request);

    /**
     * preload the wap, to speed up the authroization wap's first open
     *
     * @param request
     * @return
     */
    boolean preloadWebAuth(Authorization.Request request);

    /**
     * send request to authorize if tiktok hasnot been installed, it will get authorization through wap
     *
     * @param request
     * @return
     */
    boolean sendAuthLogin(Authorization.Request request);

    /**
     * 分享视频、图片
     *
     * share image/video
     *
     * @return
     */
    boolean share(Share.Request request);

    /**
     * 解析 share Intent 请求
     * parse share intent
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    @Deprecated
    boolean handleShareIntent(Intent intent, BDApiEventHandler eventHandler);
}