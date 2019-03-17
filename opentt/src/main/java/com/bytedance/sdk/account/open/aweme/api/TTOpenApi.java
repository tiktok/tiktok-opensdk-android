package com.bytedance.sdk.account.open.aweme.api;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.share.Share;

/**
 * 今日头条 open API
 * Created by yangzhirong on 2018/10/17.
 *
 * @author yangzhirong@bytedance.com
 */
public interface TTOpenApi {

    /**
     * 各功能要求的最低版本（抖音侧定义）
     */
    interface REQUIRED_API_VERSION {
        // 分享
        int SHARE_REQUIRED_MIN_VERSION = 2; //对应抖音5.2.0及以上
    }

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
    boolean sendInnerWebAuthRequest(SendAuth.Request request);

    /**
     * h5页授权 预加载
     *
     * @param request
     * @return
     */
    boolean preloadWebAuth(SendAuth.Request request);

    /**
     * 请求授权。如果没有安装应用。使用h5页面授权
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
     * 判断当前抖音版本是否支持分享
     * @return
     */
    boolean isAppSupportShare();

    /**
     * 解析 share Intent 请求
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    @Deprecated
    boolean handleShareIntent(Intent intent, BDApiEventHandler eventHandler);

    /*
     * 如果通过wap进行请求授权，通过该函数可以获取rul，如果不是，返回null。
     *
     * @param response
     * @return
     */
    @Nullable
    String getWapUrlIfAuthByWap(SendAuth.Response response);
}
