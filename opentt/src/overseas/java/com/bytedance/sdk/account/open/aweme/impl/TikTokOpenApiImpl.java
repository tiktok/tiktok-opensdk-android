package com.bytedance.sdk.account.open.aweme.impl;

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
import com.bytedance.sdk.account.open.aweme.api.TiktokCheckApi;
import com.bytedance.sdk.account.open.aweme.api.TiktokOpenApi;

/**
 * Tiktok授权实现类
 *
 * @author changlei@bytedance.com
 */

class TikTokOpenApiImpl implements TiktokOpenApi {

    private BDOpenApi bdOpenApi;
    private final TiktokCheckApi[] checkApis;

    static final String LOCAL_ENTRY_ACTIVITY = "bdopen.BdEntryActivity"; // 请求授权的结果回调Activity入口

    TikTokOpenApiImpl(Context context, BDOpenApi bdOpenApi) {
        this.bdOpenApi = bdOpenApi;
        checkApis = new TiktokCheckApi[] {
                new MusicallyCheckApiImpl(this.bdOpenApi),
                new TiktokCheckApiImpl(this.bdOpenApi)
        };
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

        return bdOpenApi.handleIntent(intent, eventHandler);
    }

    @Override
    public boolean isAppInstalled() {
        return findAppHasInstalled() != null;
    }

    @Override
    public boolean isAppSupportAPI() {
        return findAppHasInstalled() != null;
    }

    @Override
    public boolean openApp() {
        TiktokCheckApi appHasInstalled = findAppHasInstalled();
        return appHasInstalled != null && bdOpenApi.openApp(appHasInstalled.getPackageName());
    }

    @Override
    public int getPlatformSDKVersion() {
        TiktokCheckApi appHasInstalled = findAppHasInstalled();
        if (appHasInstalled != null) {
            return bdOpenApi.getPlatformSDKVersion(appHasInstalled.getPackageName(),
                    appHasInstalled.getRemoteEntryActivity());
        } else {
            return BDOpenConstants.META_PLATFORM_SDK_VERSION_ERROR;
        }
    }

    @Override
    public boolean sendAuthLogin(SendAuth.Request request) {
        TiktokCheckApi appHasInstalled = findAppHasInstalled();
        if (appHasInstalled != null && appHasInstalled.sendRemoteRequest(LOCAL_ENTRY_ACTIVITY, request)) {
            return true;
        } else {
            return sendInnerWebAuthRequest(request);
        }
    }

    @Override
    public boolean sendInnerWebAuthRequest(SendAuth.Request request) {
        return bdOpenApi.sendInnerWebAuthRequest(TikTokWebAuthorizeActivity.class, request);
    }

    @Override
    public boolean preloadWebAuth(SendAuth.Request request) {
        return bdOpenApi.preloadWebAuth(request, TikTokWebAuthorizeActivity.AUTH_HOST, TikTokWebAuthorizeActivity.AUTH_PATH, TikTokWebAuthorizeActivity.DOMAIN);
    }

    private boolean sendRemoteRequest(BaseReq req) {
        TiktokCheckApi appHasInstalled = findAppHasInstalled();
        return appHasInstalled != null && appHasInstalled.sendRemoteRequest(LOCAL_ENTRY_ACTIVITY, req);
    }

    @Override
    public boolean sendInnerResponse(SendAuth.Request req, BaseResp resp) {
        return bdOpenApi.sendInnerResponse(LOCAL_ENTRY_ACTIVITY, req, resp);
    }

    @Nullable
    private TiktokCheckApi findAppHasInstalled() {
        for (TiktokCheckApi checkapi : checkApis) {
            if (checkapi.couldAppBeUsedForAuthorization()) {
                return checkapi;
            }
        }
        return null;
    }
}
