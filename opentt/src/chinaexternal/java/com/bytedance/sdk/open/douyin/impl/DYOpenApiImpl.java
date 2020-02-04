package com.bytedance.sdk.open.douyin.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.base.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.authorize.handler.SendAuthDataHandler;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.handler.IDataHandler;
import com.bytedance.sdk.open.aweme.common.handler.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.aweme.share.ShareDataHandler;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.douyin.BuildConfig;
import com.bytedance.sdk.open.douyin.api.DYOpenApi;
import com.bytedance.sdk.open.douyin.ui.DYWebAuthorizeActivity;

import java.util.HashMap;
import java.util.Map;

public class DYOpenApiImpl implements DYOpenApi {

    private Context mContext;


    private Map<Integer, IDataHandler> handlerMap = new HashMap<>(2);


    private ShareImpl shareImpl;
    private AuthImpl authImpl;

    private static final String LOCAL_ENTRY_ACTIVITY = "douyinapi.DouYinEntryActivity"; // 请求授权的结果回调Activity入口
    private static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口

    private static final int TYPE_AUTH_HANDLER = 1;
    private static final int TYPE_SHARE_HANDLER = 2;

    public DYOpenApiImpl(Context context, AuthImpl authImpl, ShareImpl shareImpl) {
        this.mContext = context;
        this.shareImpl = shareImpl;
        this.authImpl = authImpl;
        handlerMap.put(TYPE_AUTH_HANDLER, new SendAuthDataHandler());
        handlerMap.put(TYPE_SHARE_HANDLER, new ShareDataHandler());

    }

    @Override
    public boolean handleIntent(Intent intent, TikTokApiEventHandler eventHandler) {
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

        int type = bundle.getInt(ParamKeyConstants.BaseParams.TYPE);//授权使用的
        if (type == 0) {
            type = bundle.getInt(ParamKeyConstants.ShareParams.TYPE);//分享使用的
        }
        switch (type) {
            case CommonConstants.ModeType.SEND_AUTH_REQUEST:
            case CommonConstants.ModeType.SEND_AUTH_RESPONSE:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
            case CommonConstants.ModeType.SHARE_CONTENT_TO_TT:
            case CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP:
                return handlerMap.get(TYPE_SHARE_HANDLER).handle(type, bundle, eventHandler);
            default:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
        }
    }

    @Override
    public boolean isAppSupportAuthorization() {
        return new DYCheckHelperImpl(mContext).isAppSupportAuthorization();

    }

    @Override
    public boolean isAppSupportShare() {
        return new DYCheckHelperImpl(mContext).isAppSupportShare();

    }

    @Override
    public boolean authorize(Authorization.Request request) {
        if (request == null) {
            return false;
        }
        IAPPCheckHelper appHasInstalled = new DYCheckHelperImpl(mContext);
        if (appHasInstalled.isAppSupportAuthorization()) {
            return authImpl.authorizeNative(request, appHasInstalled.getPackageName(), appHasInstalled.getRemoteAuthEntryActivity(), LOCAL_ENTRY_ACTIVITY, BuildConfig.SDK_NAME, BuildConfig.SDK_VERSION);
        } else {
            return sendWebAuthRequest(request);
        }
    }

    @Override
    public boolean share(Share.Request request) {
        if (request == null) {
            return false;
        }
        DYCheckHelperImpl checkHelper = new DYCheckHelperImpl(mContext);
        if (mContext != null && checkHelper.isAppSupportShare()) {
            return shareImpl.share(LOCAL_ENTRY_ACTIVITY, checkHelper.getPackageName(), REMOTE_SHARE_ACTIVITY, request,
                    checkHelper.getRemoteAuthEntryActivity(), BuildConfig.SDK_NAME, BuildConfig.SDK_VERSION);
        }
        return false;
    }

    private boolean sendWebAuthRequest(Authorization.Request request) {
        return authImpl.authorizeWeb(DYWebAuthorizeActivity.class, request);

    }
}
