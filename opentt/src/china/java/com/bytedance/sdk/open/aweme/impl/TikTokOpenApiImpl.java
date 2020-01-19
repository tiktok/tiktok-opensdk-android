package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bytedance.sdk.open.aweme.DouYinConstants;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.base.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.authorize.handler.SendAuthDataHandler;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.handler.IDataHandler;
import com.bytedance.sdk.open.aweme.common.handler.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.aweme.share.ShareDataHandler;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.aweme.common.impl.AwemeCheckHelperImpl;
import com.bytedance.sdk.open.aweme.ui.AwemeWebAuthorizeActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiktok授权实现类
 *
 * @author changlei@bytedance.com
 */

public class TikTokOpenApiImpl implements TiktokOpenApi {

    private Context mContext;


    private Map<Integer, IDataHandler> handlerMap = new HashMap<>(2);


    private ShareImpl shareImpl;
    private AuthImpl authImpl;

    static final int API_TYPE_LOGIN = 0;
    static final int API_TYPE_SHARE = 1;

    static final String LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity"; // 请求授权的结果回调Activity入口
    static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口

    private static final int TYPE_AUTH_HANDLER = 1;
    private static final int TYPE_SHARE_HANDLER = 2;

    public TikTokOpenApiImpl(Context context, AuthImpl authImpl, ShareImpl shareImpl) {
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
            case DouYinConstants.ModeType.SEND_AUTH_REQUEST:
            case DouYinConstants.ModeType.SEND_AUTH_RESPONSE:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
            case DouYinConstants.ModeType.SHARE_CONTENT_TO_TT:
            case DouYinConstants.ModeType.SHARE_CONTENT_TO_TT_RESP:
                return handlerMap.get(TYPE_SHARE_HANDLER).handle(type, bundle, eventHandler);
            default:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
        }
    }

    @Override
    public boolean isAppSupportAuthorization() {
            return new AwemeCheckHelperImpl(mContext).isAppSupportAuthorization();

    }

    @Override
    public boolean isAppSupportShare() {
            return new AwemeCheckHelperImpl(mContext).isAppSupportShare();

    }

    @Override
    public boolean authorize(Authorization.Request request) {
        IAPPCheckHelper appHasInstalled;

            appHasInstalled = new AwemeCheckHelperImpl(mContext);
            if (!appHasInstalled.isAppSupportAuthorization()) {
                // 这个时候抖音没安装所以要走web授权
                appHasInstalled = null;
            }

        if (appHasInstalled != null && authImpl.authorizeNative(request, appHasInstalled.getPackageName(), appHasInstalled.getRemoteAuthEntryActivity(), LOCAL_ENTRY_ACTIVITY)) {
            return true;
        } else {
            return sendWebAuthRequest(request);
        }
    }

    @Override
    public boolean share(Share.Request request) {
        if (request == null) {
            return false;
        }

        // 适配抖音
            AwemeCheckHelperImpl checkHelper = new AwemeCheckHelperImpl(mContext);
            if (mContext != null && checkHelper.isAppSupportShare()) {
                return shareImpl.share(LOCAL_ENTRY_ACTIVITY, checkHelper.getPackageName(), REMOTE_SHARE_ACTIVITY, request,
                        checkHelper.getRemoteAuthEntryActivity());

        }

        return false;
    }

    private boolean sendWebAuthRequest(Authorization.Request request) {
            return authImpl.authorizeWeb(AwemeWebAuthorizeActivity.class, request);

    }
}
