package com.bytedance.sdk.open.aweme.api;
import android.content.Intent;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.BDApiEventHandler;

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

    /**
     * 目标应用是否已经安装
     *
     * 应部分厂商需求打开此api. 不太建议使用;
     * 因为对应的isAppSupportShare及isAppSupportAuthorization
     * 包含了此功能且带有版本判断. 建议用另外两个接口.
     * @return
     */
    boolean isAppInstalled(int targetApp);

    /**
     * send request to authorize if tiktok hasnot been installed, it will get authorization through wap
     *
     * @param request
     * @return
     */
    boolean authorize(Authorization.Request request);

    /**
     * 分享视频、图片
     *
     * share image/video
     *
     * @return
     */
    boolean share(Share.Request request);


}