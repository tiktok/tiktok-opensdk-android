package com.bytedance.sdk.open.aweme.common.api;

import android.app.Activity;
import android.content.Intent;

import com.bytedance.sdk.open.aweme.common.handler.BDApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.authorize.model.SendAuth;


/**
 * 基本的 openAPI . 对外 openAPI 在此基础上进行封装
 * Created by yangzhirong on 2018/9/26.
 */
public interface BDOpenApi {

    /**
     * 解析 Intent 为对应的 Request/Response
     *
     * @param intent
     * @param eventHandler
     * @return
     */
    boolean handleIntent(Intent intent, BDApiEventHandler eventHandler);

    /**
     * 应用是否已安装
     *
     * @param platformPackageName 目标应用包名
     * @return
     */
    boolean isAppInstalled(String platformPackageName);

    /**
     * 判断目标应用的 platformAPI 是否满足所需要的 版本号
     *
     * @param platformPackageName 目标应用包名
     * @param remoteRequestEntry  目标应用的入口 Activity
     * @param requiredApi         期望的最低版本号
     * @return
     */
    boolean isAppSupportAPI(String platformPackageName, String remoteRequestEntry, int requiredApi);

    /**
     * 打开目标应用
     *
     * @param platformPackageName 目标应用包名
     * @return
     */
    boolean openApp(String platformPackageName);
    /**
     * 发送一个 Request 请求到目标应用
     *
     * @param remotePackageName  目标应用包名
     * @param remoteRequestEntry 目标应用入口 Activity. 用于接收 Request
     * @param req                实际请求数据
     * @param localEntry         当前应用的入口 Activity.用于接收 Response
     * @return
     */
    boolean sendRemoteRequest(String localEntry, String remotePackageName, String remoteRequestEntry, BaseReq req);
    /**
     * 向当前应用的入口 Activity 发送一个 Response ( 比如 webauth 的 Response)
     *
     * @param localEntry 当前应用的入口 Activity.用于接收 Response
     * @param req        实际请求数据
     * @param resp       实际 Response 数据
     * @return
     */
    boolean sendInnerResponse(String localEntry, SendAuth.Request req, BaseResp resp);

    /**
     * 获得目标应用的 platform 版本号
     *
     * @param platformPackageName 目标应用包名
     * @param remoteRequestEntry  目标应用入口 Activity 读取其中的 meta-info
     * @return
     */
    int getPlatformSDKVersion(String platformPackageName, String remoteRequestEntry);

    /**
     * 验证目标应用的签名是否为 sign
     *
     * @param platformPackageName 目标应用包名
     * @param sign                需验证签名
     * @return
     */
    boolean validateSign(String platformPackageName, String sign);

    /**
     * 使用 web 授权
     *
     * @param localWeb web 授权 Activity Class
     * @param request  实际数据
     * @return
     */
    <T extends Activity> boolean sendInnerWebAuthRequest(Class<T> localWeb, SendAuth.Request request);

    /**
     *  web 授权 预加载
     *
     * @param request  实际数据
     * @param host     web 授权页 host
     * @param path     web 授权页 path
     * @param domain   web 授权页 domain
     * @return
     */
    boolean preloadWebAuth(SendAuth.Request request, String host, String path, String domain);
}

