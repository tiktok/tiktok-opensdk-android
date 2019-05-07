package com.bytedance.sdk.account.open.aweme.impl;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.account.bdopen.impl.BaseBDWebAuthorizeActivity;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.constants.BDOpenConstants;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;
import com.bytedance.sdk.account.open.aweme.utils.ViewUtils;

import static com.bytedance.sdk.account.open.aweme.impl.TTOpenApiImpl.WAP_AUTHORIZE_URL;

/**
 * Created by yangzhirong on 2018/10/10.
 */
public class TTWebAuthorizeActivity extends BaseBDWebAuthorizeActivity {

    public static final String AUTH_HOST = "open.douyin.com";
    public static final String DOMAIN = "api.snssdk.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";

    private AlertDialog mDialog;

    private TTOpenApi ttOpenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = TTOpenApiFactory.create(this);
        super.onCreate(savedInstanceState);

        ViewUtils.setStatusBarColor(this, Color.parseColor("#161823"));
    }

    /**
     * 显示网络错误对话框
     */
    @Override
    protected void showNetworkErrorDialog(final int errCode) {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }
        if (mDialog == null) {
            View mDialogView = LayoutInflater.from(this).inflate(getResources().getIdentifier("douyin_layout_open_network_error_dialog", "layout", getPackageName()), null, false);
            mDialogView.findViewById(getResources().getIdentifier("douyin_confirm", "id", getPackageName())).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancel(errCode);
                }
            });
            mDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(mDialogView)
                    .create();
        }
        mDialog.show();
    }

    @Override
    protected View getLoadingView(ViewGroup root) {
        View loadingView = LayoutInflater.from(this).inflate(getResources().getIdentifier("douyin_layout_open_loading_view", "layout", getPackageName()), root, false);
        return loadingView;
    }

    @Override
    protected View getHeaderView(ViewGroup root) {
        View headerView = LayoutInflater.from(this).inflate(getResources().getIdentifier("douyin_layout_open_web_header_view", "layout", getPackageName()), root, false);
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
        if (resp != null && mContentWebView != null) {
            if (resp.extras == null) {
                resp.extras = new Bundle();
            }
            resp.extras.putString(WAP_AUTHORIZE_URL, mContentWebView.getUrl());
        }
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
            mContainer.setBackgroundColor(Color.parseColor("#161823"));
        }
    }

    @Override
    protected String errorCode2Message(int errorCode) {
        /**
         * 示范。。 根据 业务定制返回 error message
         */
        switch (errorCode) {
            default:
                return getString(getResources().getIdentifier("douyin_error_tips_common", "string", getPackageName()));
        }
    }
}
