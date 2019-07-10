package com.bytedance.sdk.open.aweme.api;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.BDApiEventHandler;
import com.bytedance.sdk.open.aweme.share.Share;

/**
 * 抖音 open API
 * Created by yangzhirong on 2018/10/17.
 *
 * @author yangzhirong@bytedance.com
 */
public interface DYOpenApi {

    /**
     * 解析 Intent 请求(针对auth)
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    boolean handleIntent(Intent intent, BDApiEventHandler eventHandler);

    /**
     * 目标应用是否已经按照
     *
     * @return
     */
    boolean isAppInstalled();

    /**
     * 验证应用签名
     *
     * @return
     */
    boolean validateSign();


    boolean isAppSupportAuthorization(int targetApp);

    /**
     *
     * @param targetApp tiktok、tiktok-m、抖音
     * @return
     */
    boolean isAppSupportShare(int targetApp);

    /**
     * 请求授权。如果没有安装应用。使用h5页面授权
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

}
