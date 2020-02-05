package com.bytedance.sdk.open.douyin.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.authorize.ui.BaseWebAuthorizeActivity;
import com.bytedance.sdk.open.aweme.base.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.utils.ViewUtils;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;

/**
 * DouYin web authorization
 * author: ChangLei
 * since: 2019/5/17
 */
public class DouYinWebAuthorizeActivity extends BaseWebAuthorizeActivity {

    public static final String AUTH_HOST = "open.douyin.com";
    public static final String DOMAIN = "api.snssdk.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";

    private DouYinOpenApi ttOpenApi;
    private String mCommonParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = DouYinOpenApiFactory.create(this);
        super.onCreate(savedInstanceState);
        ViewUtils.setStatusBarColor(this, Color.TRANSPARENT);
    }

    @Override
    protected void configWebView() {
        mContentWebView.setWebViewClient(new AuthClient());
    }

    private class AuthClient extends AuthWebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!TextUtils.isEmpty(mCommonParams)) {
                injectCommonParams();
            }
        }
    }

    private void injectCommonParams() {
        String command = "javascript:(function () {" +
                "window.secureCommonParams ='" + mCommonParams + "';" +
                "})();";
        mContentWebView.loadUrl(command);

    }


    @Override
    protected boolean isNetworkAvailable() {
        return true;
    }

    @Override
    protected boolean handleIntent(Intent intent, IApiEventHandler eventHandler) {
        Bundle bundle = intent.getBundleExtra(ParamKeyConstants.BaseParams.EXTRA);
        if (bundle != null) {
            mCommonParams = bundle.getString("internal_secure_common_params");
        }

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
