package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bytedance.sdk.account.bdopen.api.BDOpenApi;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.constants.BDOpenConstants;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.open.aweme.DYOpenApi;
import com.bytedance.sdk.open.aweme.DYOpenConstants;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.aweme.share.ShareImpl;

/**
 * Created by yangzhirong on 2018/10/17.
 * 此类对应抖音版本;
 */
class DYOpenApiImpl implements DYOpenApi {

    public static final String WAP_AUTHORIZE_URL = "wap_authorize_url";

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

    DYOpenApiImpl(Context context, BDOpenApi bdOpenApi, ShareImpl shareImpl) {
        this.mContext = context;
        this.bdOpenApi = bdOpenApi;
        this.shareImpl = shareImpl;
    }

    @Override
    public boolean handleIntent(Intent intent, BDApiEventHandler eventHandler) {
        if (eventHandler == null) {
            return false;
        }
        if (intent == null) {
            eventHandler.onErrorIntent(intent);
            return false;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            eventHandler.onErrorIntent(intent);
            return false;
        }

        int type = bundle.getInt(BDOpenConstants.Params.TYPE) == 0 ? bundle.getInt(DYOpenConstants.Params.TYPE) : 0;
        return distributionIntent(type, intent, eventHandler);
    }

    @Override public boolean isAppSupportAuthorization() {
        return isAppSupportAPI(DYOpenConstants.REQUIRED_API_VERSION.AUTH_REQUIRE_API) && validateSign();
    }

    @Override public boolean isAppSupportShare(int targetApp) {
        return isAppSupportAPI(DYOpenConstants.REQUIRED_API_VERSION.SHARE_REQUIRED_MIN_VERSION);
    }

    private boolean distributionIntent(int type, Intent intent, BDApiEventHandler eventHandler) {
        switch (type) {
            case BDOpenConstants.ModeType.SEND_AUTH_REQUEST:
            case BDOpenConstants.ModeType.SEND_AUTH_RESPONSE:
                return bdOpenApi.handleIntent(intent, eventHandler);
            case DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY:
            case DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY_RESP:
                return shareImpl.handleShareIntent(intent, eventHandler);
            default:
                return bdOpenApi.handleIntent(intent, eventHandler);
        }
    }

    @Override @Deprecated
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
        if (isAppSupportAPI(DYOpenConstants.REQUIRED_API_VERSION.AUTH_REQUIRE_API) && validateSign() &&
                sendRemoteRequest(request)) {
            return true;
        } else {
            return sendInnerWebAuthRequest(request);
        }
    }

    @Nullable
    @Override
    public String getWapUrlIfAuthByWap(SendAuth.Response response) {
        if (response != null && response.extras != null && response.extras.containsKey(WAP_AUTHORIZE_URL)) {
            return response.extras.getString(WAP_AUTHORIZE_URL, "");
        }
        return null;
    }

    @Override
    public boolean share(Share.Request request) {
        if (isAppInstalled() && isAppSupportShare()) {
            return shareImpl.share(LOCAL_ENTRY_ACTIVITY, REMOTE_ENTRY_PACKAGE, REMOTE_SHARE_ACTIVITY, request);
        } else {
            return false;
        }
    }

    @Override
    public boolean isAppSupportShare() {
        return isAppSupportAPI(DYOpenConstants.REQUIRED_API_VERSION.SHARE_REQUIRED_MIN_VERSION);
    }

    @Override
    @Deprecated
    public boolean handleShareIntent(Intent intent, BDApiEventHandler eventHandler) {
        return shareImpl.handleShareIntent(intent, eventHandler);
    }

    @Override
    public boolean sendInnerWebAuthRequest(SendAuth.Request request) {
        return bdOpenApi.sendInnerWebAuthRequest(TTWebAuthorizeActivity.class, request);
    }

    @Override
    public boolean preloadWebAuth(SendAuth.Request request) {
        return bdOpenApi.preloadWebAuth(request, TTWebAuthorizeActivity.AUTH_HOST, TTWebAuthorizeActivity.AUTH_PATH,
                TTWebAuthorizeActivity.DOMAIN);
    }
}
