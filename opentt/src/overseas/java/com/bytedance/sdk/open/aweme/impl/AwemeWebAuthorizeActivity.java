package com.bytedance.sdk.open.aweme.impl;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bytedance.sdk.account.bdopen.impl.BaseBDWebAuthorizeActivity;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.constants.BDOpenConstants;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.utils.ViewUtils;

/**
 * 主要功能：该类是为了在Tiktok SDK中兼容抖音的授权逻辑
 * author: ChangLei
 * since: 2019/5/17
 */
public class AwemeWebAuthorizeActivity extends BaseBDWebAuthorizeActivity {

    public static final String AUTH_HOST = "open.douyin.com";
    public static final String DOMAIN = "api.snssdk.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";

    private TiktokOpenApi ttOpenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = TikTokOpenApiFactory.create(this);
        super.onCreate(savedInstanceState);
        mContainer.setBackgroundColor(Color.parseColor("#161823"));
        ViewUtils.setStatusBarColor(this, Color.parseColor("#161823"));
    }

    @Override
    protected View getLoadingView(ViewGroup root) {
        View loadingView = LayoutInflater.from(this).inflate(getResources().getIdentifier("tiktok_layout_open_loading_view_for_aweme", "layout", getPackageName()), root, false);
        return loadingView;
    }

    @Override
    protected View getHeaderView(ViewGroup root) {
        View headerView = LayoutInflater.from(this).inflate(getResources().getIdentifier("tiktok_layout_open_web_header_view_for_aweme", "layout", getPackageName()), root, false);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel(BDOpenConstants.ErrorCode.ERROR_CODE_CANCEL);
            }
        });
        return headerView;
    }

    @Override
    protected boolean isNetworkAvailable() {
        return true;
    }

    @Override
    protected boolean handleIntent(Intent intent, BDApiEventHandler eventHandler) {
        return ttOpenApi.handleIntent(intent, eventHandler);
    }

    @Override
    protected void sendInnerResponse(SendAuth.Request req, BaseResp resp) {
        ttOpenApi.sendInnerResponse(req, resp);
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
        // 目前Tiktok没有自定义的错误码，不需要转换
        return "";
    }
}
