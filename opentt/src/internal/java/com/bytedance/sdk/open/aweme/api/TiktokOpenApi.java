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
    boolean authorizeNative(Authorization.Request request);


    boolean authorizeWeb(Authorization.Request request);

    boolean authorizeWeb(Authorization.Request request, Class cla);


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
     * check if the application supports sharing
     * @return
     */
    boolean isAppSupportShare();

    /**
     * 目标应用是否已经安装
     *
     * 应部分厂商需求打开此api. 不太建议使用;
     * 因为对应的isAppSupportShare及isAppSupportAuthorization
     * 包含了此功能且带有版本判断. 建议用另外两个接口.
     * @return
     */
    boolean isAppInstalled();



    /**
     * 如果通过wap进行请求授权，通过该函数可以获取rul，如果不是，返回null。
     * If authorization is requested through wap, rul can be obtained through this function, and if not, null can be returned.
     * @param response
     * @return
     */
    String getWapUrlIfAuthByWap(Authorization.Response response);

    /**
     * parse response or request data in intent
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    boolean handleIntent(Intent intent, TikTokApiEventHandler eventHandler);


    String getSdkVersion();

}