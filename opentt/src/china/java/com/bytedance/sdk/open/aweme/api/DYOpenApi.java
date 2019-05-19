package com.bytedance.sdk.open.aweme.api;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.open.aweme.authorize.Authorization;
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
     * 是否支持API版本
     *
     * @param requiredApi
     * @return
     */
    boolean isAppSupportAPI(int requiredApi);

    /**
     * 打开目标应用
     *
     * @return
     */
    boolean openApp();

    /**
     * 向目标应用发送请求
     *
     * @param req
     * @return
     */
    boolean sendRemoteRequest(BaseReq req);

    /**
     * 向应用内发送回调 (比如.web授权回调给本应用)
     *
     * @param resp
     * @return
     */
    boolean sendInnerResponse(SendAuth.Request req, BaseResp resp);

    /**
     * 获取对应SDK版本号
     *
     * @return
     */
    int getPlatformSDKVersion();

    /**
     * 验证应用签名
     *
     * @return
     */
    boolean validateSign();

    /**
     * h5页授权
     *
     * @param request
     * @return
     */
    boolean sendInnerWebAuthRequest(Authorization.Request request);

    /**
     * h5页授权 预加载
     *
     * @param request
     * @return
     */
    boolean preloadWebAuth(Authorization.Request request);

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

    /*
     * 如果通过wap进行请求授权，通过该函数可以获取rul，如果不是，返回null。
     *
     * @param response
     * @return
     */
    @Nullable
    String getWapUrlIfAuthByWap(SendAuth.Response response);

    /**
     * 接口将废弃，尽快切换到isAppSupportShare(int targetApp)
     * @return
     */
    @Deprecated
    boolean isAppSupportShare();

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
