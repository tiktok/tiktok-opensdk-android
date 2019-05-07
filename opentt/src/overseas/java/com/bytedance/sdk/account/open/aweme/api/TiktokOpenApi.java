package com.bytedance.sdk.account.open.aweme.api;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;

/**
 * Tiktok open API
 *
 * @author changlei@bytedance.com
 */
public interface TiktokOpenApi {

    /**
     * 各功能要求的最低版本（抖音侧定义）
     */
    interface REQUIRED_API_VERSION {
        // 分享
        int SHARE_REQUIRED_MIN_VERSION = 1; //对应aweme 600及以上
    }

    /**
     * parse Intent request for authorization from tiktok
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    boolean handleIntent(Intent intent, BDApiEventHandler eventHandler);

    /**
     * whether tiktok has been installed
     *
     * @return
     */
    boolean isAppInstalled();

    /**
     * whether tiktok is support current version of open sdk
     *
     * @return
     */
    boolean isAppSupportAPI();

    /**
     * open tiktok app
     *
     * @return
     */
    boolean openApp();

    /**
     * get the platform version of tiktok using
     *
     * @return
     */
    int getPlatformSDKVersion();

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
}
