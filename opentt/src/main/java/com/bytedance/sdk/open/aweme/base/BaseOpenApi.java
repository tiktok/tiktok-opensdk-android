package com.bytedance.sdk.open.aweme.base;

import android.content.Intent;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.open.aweme.share.Share;

/**
 * Created by colinyu on 2019/5/8.
 */
public interface BaseOpenApi {

    /**
     * parse Intent request for authorization from tiktok
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    boolean handleIntent(Intent intent, BDApiEventHandler eventHandler);

    boolean isAppSupportAuthorization();

    boolean isAppSupportShare(int targetApp);

    boolean sendInnerResponse(SendAuth.Request req, BaseResp resp);

    /**
     * to authorize from wap
     *
     * @param request
     * @return
     */
    boolean sendInnerWebAuthRequest(SendAuth.Request request);

    /**
     * preload the wap, to speed up the authroization wap's first open
     *
     * @param request
     * @return
     */
    boolean preloadWebAuth(SendAuth.Request request);

    /**
     * send request to authorize if tiktok hasnot been installed, it will get authorization through wap
     *
     * @param request
     * @return
     */
    boolean sendAuthLogin(SendAuth.Request request);

    /**
     * 分享视频、图片
     *
     * @return
     */
    boolean share(Share.Request request);

    /**
     * 解析 share Intent 请求
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    @Deprecated
    boolean handleShareIntent(Intent intent, BDApiEventHandler eventHandler);
}
