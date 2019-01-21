package com.bytedance.sdk.account.open.aweme.impl;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.bytedance.sdk.account.bdopen.impl.BaseBDWebAuthorizeActivity;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.R;
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

    private boolean isShowNetworkError = false;

    private TTOpenApi ttOpenApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttOpenApi = TTOpenApiFactory.create(this);
        super.onCreate(savedInstanceState);

        ViewUtils.setStatusBarColor(this, Color.parseColor("#161823"));
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
    protected void setBackBtnStyle() {
        if (mCancelTxt != null) {
            mCancelTxt.setVisibility(View.VISIBLE);
            mCancelTxt.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.selector_web_authorize_titlebar_back);
            if (drawable != null) {
                int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
                drawable.setBounds(0, 0, size, size);
            }
            mCancelTxt.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @Override
    protected void setHeaderViewBgColor() {
        if (mHeaderView != null) {
            mHeaderView.setBackgroundColor(Color.parseColor("#161823"));
        }
    }

    @Override
    protected void setContainerViewBgColor() {
        if (mContainer != null) {
            mContainer.setBackgroundColor(Color.parseColor("#161823"));
        }
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
