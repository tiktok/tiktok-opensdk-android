package com.bytedance.sdk.account.open.aweme.impl;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.account.bdopen.impl.BaseBDWebAuthorizeActivity;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.constants.BDAuthConstants;
import com.bytedance.sdk.account.common.constants.BDOpenConstants;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.account.open.aweme.utils.ViewUtils;


/**
 * tiktok authroize wap
 *
 * @author changlei@bytedance.com
 */
public class TikTokWebAuthorizeActivity extends BaseBDWebAuthorizeActivity {

    public static final String AUTH_HOST = "open-api.musical.ly";
    public static final String DOMAIN = "api.snssdk.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";

    private AlertDialog mDialog;

    private TiktokOpenApi ttOpenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = TikTokOpenApiFactory.create(this);
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mContentWebView.setWebContentsDebuggingEnabled(true);
        }
        ViewUtils.setStatusBarColor(this, Color.parseColor("#FFFFFFFF"));
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
            View mDialogView = LayoutInflater.from(this).inflate(getResources().getIdentifier("tiktok_layout_open_network_error_dialog", "layout", getPackageName()), null, false);
            mDialogView.findViewById(getResources().getIdentifier("tiktok_confirm", "id", getPackageName())).setOnClickListener(new View.OnClickListener() {
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
        View loadingView = LayoutInflater.from(this).inflate(getResources().getIdentifier("tiktok_layout_open_loading_view", "layout", getPackageName()), root, false);
        return loadingView;
    }

    @Override
    protected View getHeaderView(ViewGroup root) {
        View headerView = LayoutInflater.from(this).inflate(getResources().getIdentifier("tiktok_layout_open_web_header_view", "layout", getPackageName()), root, false);
        headerView.findViewById(getResources().getIdentifier("tiktok_cancel", "id", getPackageName())).setOnClickListener(new View.OnClickListener() {
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
        /**
         * 示范。。 根据 业务定制返回 error message
         */
        switch (errorCode) {
            default:
                return getString(getResources().getIdentifier("tiktok_error_tips_common", "string", getPackageName()));
        }
    }
}
