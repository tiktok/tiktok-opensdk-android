package com.bytedance.sdk.open.douyin.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.authorize.ui.BaseWebAuthorizeActivity;
import com.bytedance.sdk.open.aweme.common.handler.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.utils.ViewUtils;
import com.bytedance.sdk.open.douyin.DYApiFactory;
import com.bytedance.sdk.open.douyin.api.DYOpenApi;

/**
 * DouYin web authorization
 * author: ChangLei
 * since: 2019/5/17
 */
public class DYWebAuthorizeActivity extends BaseWebAuthorizeActivity {

    public static final String AUTH_HOST = "open.douyin.com";
    public static final String DOMAIN = "api.snssdk.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";

    private DYOpenApi ttOpenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = DYApiFactory.create(this);
        super.onCreate(savedInstanceState);
        ViewUtils.setStatusBarColor(this, Color.TRANSPARENT);
    }

    @Override
    protected boolean isNetworkAvailable() {
        return true;
    }

    @Override
    protected boolean handleIntent(Intent intent, TikTokApiEventHandler eventHandler) {
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

        sendInnerResponse(LOCAL_ENTRY_ACTIVITY, req, resp);
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
            mContainer.setBackgroundColor(Color.parseColor("#161823"));
        }
    }

    @Override
    protected String errorCode2Message(int errorCode) {
        return "";
    }
}
