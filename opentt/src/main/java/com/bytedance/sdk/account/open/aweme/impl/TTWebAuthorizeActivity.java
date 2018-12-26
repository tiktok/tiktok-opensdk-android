package com.bytedance.sdk.account.open.aweme.impl;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.bytedance.sdk.account.bdopen.impl.BaseBDWebAuthorizeActivity;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.R;
import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;

/**
 * Created by yangzhirong on 2018/10/10.
 */
public class TTWebAuthorizeActivity extends BaseBDWebAuthorizeActivity {

    public static final String AUTH_HOST = "open.douyin.com";
    public static final String DOMAIN = "api.snssdk.com";
    public static final String AUTH_PATH = "/platform/oauth/connect/";

    private boolean isShowNetworkError = false;

    private TTOpenApi ttOpenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = TTOpenApiFactory.create(this);
        super.onCreate(savedInstanceState);
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

    /**
     * 显示网络错误对话框
     */
    @Override
    protected void showNetworkErrorDialog() {
        if (isShowNetworkError) {
            return;
        }
        isShowNetworkError = true;
        //hideProgressBar();
        View mDialogView = LayoutInflater.from(this).inflate(R.layout.network_error_dialog, null, false);
        mDialogView.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onCancel();
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(mDialogView)
                .create();
        dialog.show();
    }

    @Override
    protected String errorCode2Message(int errorCode) {
        /**
         * 示范。。 根据 业务定制返回 error message
         */
        switch (errorCode) {
            default:
                return getString(R.string.error_tips_common);
        }
    }
}
