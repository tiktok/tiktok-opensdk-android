package com.bytedance.sdk.account.open.aweme.impl;

import android.content.Context;
import android.content.Intent;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;
import com.bytedance.sdk.account.open.aweme.share.Share;
import com.bytedance.sdk.account.open.aweme.share.ShareImpl;

/**
 * Created by yangzhirong on 2018/10/17.
 */

class TTOpenApiImpl implements TTOpenApi {

    private Context mContext;
    private BDOpenApi bdOpenApi;
    private ShareImpl shareImpl;

    /**
     * 以下参数  需要改成提供授权方App的
     * 此处为头条App的
     */
    static final String VALIDATE_SIGNATURE = "aea615ab910015038f73c47e45d21466"; // 授权方签名校验码
    static final String REMOTE_ENTRY_PACKAGE = "com.ss.android.ugc.aweme"; // 授权方包名
    static final String REMOTE_ENTRY_ACTIVITY = "openauthorize.AwemeAuthorizedActivity"; // 提供授权的Activity入口
    static final String LOCAL_ENTRY_ACTIVITY = "bdopen.BdEntryActivity"; // 请求授权的结果回调Activity入口
    static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口

    static final int REQUIRE_API = 1; // 用于验证api版本是否支持

    TTOpenApiImpl(Context context, BDOpenApi bdOpenApi, ShareImpl shareImpl) {
        this.mContext = context;
        this.bdOpenApi = bdOpenApi;
        this.shareImpl = shareImpl;
    }

    @Override
    public boolean handleIntent(Intent intent, BDApiEventHandler eventHandler) {
        return bdOpenApi.handleIntent(intent, eventHandler);
    }

    @Override
    public boolean isAppInstalled() {
        return bdOpenApi.isAppInstalled(REMOTE_ENTRY_PACKAGE);
    }

    @Override
    public boolean isAppSupportAPI(int requiredApi) {
        return bdOpenApi.isAppSupportAPI(REMOTE_ENTRY_PACKAGE, REMOTE_ENTRY_ACTIVITY, requiredApi);
    }

    @Override
    public boolean openApp() {
        return bdOpenApi.openApp(REMOTE_ENTRY_PACKAGE);
    }

    @Override
    public boolean sendRemoteRequest(BaseReq req) {
        return bdOpenApi.sendRemoteRequest(LOCAL_ENTRY_ACTIVITY, REMOTE_ENTRY_PACKAGE, REMOTE_ENTRY_ACTIVITY, req);
    }

    @Override
    public boolean sendInnerResponse(SendAuth.Request req, BaseResp resp) {
        return bdOpenApi.sendInnerResponse(LOCAL_ENTRY_ACTIVITY, req, resp);
    }

    @Override
    public int getPlatformSDKVersion() {
        return bdOpenApi.getPlatformSDKVersion(REMOTE_ENTRY_PACKAGE, REMOTE_ENTRY_ACTIVITY);
    }

    @Override
    public boolean validateSign() {
        return bdOpenApi.validateSign(REMOTE_ENTRY_PACKAGE, VALIDATE_SIGNATURE);
    }

    @Override
    public boolean sendAuthLogin(SendAuth.Request request) {
        if (isAppSupportAPI(REQUIRE_API) && validateSign() && sendRemoteRequest(request)) {
            return true;
        } else {
            return sendInnerWebAuthRequest(request);
        }
    }

    @Override
    public boolean share(Share.Request request) {
        return shareImpl.share(LOCAL_ENTRY_ACTIVITY, REMOTE_ENTRY_PACKAGE, REMOTE_SHARE_ACTIVITY, request);
    }

    @Override
    public boolean handleShareIntent(Intent intent, BDApiEventHandler eventHandler) {
        return shareImpl.handleShareIntent(intent, eventHandler);
    }

    @Override
    public boolean sendInnerWebAuthRequest(SendAuth.Request request) {
        return bdOpenApi.sendInnerWebAuthRequest(TTWebAuthorizeActivity.class, request);
    }

    @Override
    public boolean preloadWebAuth(SendAuth.Request request) {
        return bdOpenApi.preloadWebAuth(request, TTWebAuthorizeActivity.AUTH_HOST, TTWebAuthorizeActivity.AUTH_PATH, TTWebAuthorizeActivity.DOMAIN);
    }
}
