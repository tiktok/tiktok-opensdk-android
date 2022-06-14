package com.bytedance.sdk.open.tiktok.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.authorize.ui.BaseWebAuthorizeActivity;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.model.BaseResp;
import com.bytedance.sdk.open.tiktok.utils.ViewUtils;
import com.bytedance.sdk.open.tiktok.TikTokOpenApiFactory;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;


/**
 * tiktok authroize wap
 *
 * @author changlei@bytedance.com
 */
public class TikTokWebAuthorizeActivity extends BaseWebAuthorizeActivity {

    public static final String AUTH_HOST = "open-api.tiktok.com";
    public static final String DOMAIN = "open-api.tiktok.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";
    protected static final String LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity"; // 请求授权的结果回调Activity入口
    
    private TikTokOpenApi ttOpenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = TikTokOpenApiFactory.create(this);
        super.onCreate(savedInstanceState);
        ViewUtils.Companion.setStatusBarColor(this, Color.TRANSPARENT);
    }

    @Override
    protected boolean isNetworkAvailable() {
        return true;
    }

    @Override
    protected boolean handleIntent(Intent intent, IApiEventHandler eventHandler) {
        return ttOpenApi.handleIntent(intent, eventHandler);
    }

    @Override
    protected void sendInnerResponse(Authorization.Request req, BaseResp resp) {
        if (resp != null && mContentWebView != null) {
            if (resp.extras == null) {
                resp.extras = new Bundle();
            }
            resp.extras.putString(WAP_AUTHORIZE_URL, mContentWebView.getUrl());
        }

       sendInnerResponse(LOCAL_ENTRY_ACTIVITY,req, resp);
    }

    @Override
    protected String getHost() {
        return AUTH_HOST;
    }

    @Override
    protected String getAuthPath() {
        return AUTH_PATH;
    }

    @Override
    protected String getDomain() {
        return DOMAIN;
    }

    @Override
    protected void setContainerViewBgColor() {
        if (mContainer != null) {
            mContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    protected String errorCode2Message(int errorCode) {
        return "";
    }
}
