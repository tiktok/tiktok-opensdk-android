package com.bytedance.sdk.open.tiktok.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.tiktok.authorize.AuthImpl;
import com.bytedance.sdk.open.tiktok.authorize.handler.SendAuthDataHandler;
import com.bytedance.sdk.open.tiktok.authorize.model.Authorization;
import com.bytedance.sdk.open.tiktok.base.IAPPCheckHelper;
import com.bytedance.sdk.open.tiktok.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler;
import com.bytedance.sdk.open.tiktok.share.Share;
import com.bytedance.sdk.open.tiktok.share.ShareDataHandler;
import com.bytedance.sdk.open.tiktok.share.ShareImpl;
import com.bytedance.sdk.open.tiktok.share.ShareRequest;
import com.bytedance.sdk.open.tiktok.BuildConfig;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.helper.MusicallyCheckHelperImpl;
import com.bytedance.sdk.open.tiktok.helper.TikTokCheckHelperImpl;
import com.bytedance.sdk.open.tiktok.ui.TikTokWebAuthorizeActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiktok授权实现类
 *
 * @author changlei@bytedance.com
 */

public class TikTokOpenApiImpl implements TikTokOpenApi {

    private Context mContext;
    private final IAPPCheckHelper[] mAuthcheckApis;
    private final IAPPCheckHelper[] mSharecheckApis;


    private Map<Integer, IDataHandler> handlerMap = new HashMap<>(2);


    private ShareImpl shareImpl;
    private AuthImpl authImpl;

    private static final int API_TYPE_LOGIN = 0;
    private static final int API_TYPE_SHARE = 1;

    private static final String LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity"; // 请求授权的结果回调Activity入口
    private static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口

    private static final int TYPE_AUTH_HANDLER = 1;
    private static final int TYPE_SHARE_HANDLER = 2;

    public TikTokOpenApiImpl(Context context, AuthImpl authImpl, ShareImpl shareImpl) {
        this.mContext = context;
        this.shareImpl = shareImpl;
        this.authImpl = authImpl;
        handlerMap.put(TYPE_AUTH_HANDLER, new SendAuthDataHandler());
        handlerMap.put(TYPE_SHARE_HANDLER, new ShareDataHandler());
        mAuthcheckApis = new IAPPCheckHelper[]{
                new MusicallyCheckHelperImpl(context),
                new TikTokCheckHelperImpl(context)
        };

        mSharecheckApis = new IAPPCheckHelper[]{
                new MusicallyCheckHelperImpl(context),
                new TikTokCheckHelperImpl(context)
        };

    }

    @Override
    public boolean handleIntent(Intent intent, IApiEventHandler eventHandler) {
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
    public boolean authorizeWeb(Authorization.Request request, Class cla) {
        return sendWebAuthRequest(request, cla);

    }

    @Override
    public boolean isAppInstalled() {
        for (IAPPCheckHelper checkapi : mAuthcheckApis) {
            if (checkapi.isAppInstalled()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getSdkVersion() {
        return BuildConfig.SDK_OVERSEA_VERSION;
    }

    private boolean sendWebAuthRequest(Authorization.Request request, Class cla) {
        return authImpl.authorizeWeb(cla, request);
    }

    @Override
    public boolean isAppSupportAuthorization() {
        return getSupportApiAppInfo(API_TYPE_SHARE) != null;
    }

    @Override
    public boolean isAppSupportShare() {
        return getSupportApiAppInfo(API_TYPE_SHARE) != null;
    }

    @Override
    public boolean authorize(Authorization.Request request) {
        IAPPCheckHelper appHasInstalled = getSupportApiAppInfo(API_TYPE_LOGIN);

        if (appHasInstalled != null) {
            return authImpl.authorizeNative(request, appHasInstalled.getPackageName(), appHasInstalled.getRemoteAuthEntryActivity(), LOCAL_ENTRY_ACTIVITY, BuildConfig.SDK_OVERSEA_NAME, BuildConfig.SDK_OVERSEA_VERSION);
        } else {
            return sendWebAuthRequest(request);
        }
    }

    @Override
    public boolean authorizeWeb(Authorization.Request request) {
        return sendWebAuthRequest(request);
    }

    @Override
    public boolean share(Share.Request request) {
        if (request == null) {
            return false;
        }

        if (isAppSupportShare()) {
            String remotePackage = getSupportApiAppInfo(API_TYPE_SHARE).getPackageName();
            return shareImpl.share(LOCAL_ENTRY_ACTIVITY, remotePackage, REMOTE_SHARE_ACTIVITY, request,
                    getSupportApiAppInfo(API_TYPE_SHARE).getRemoteAuthEntryActivity(), BuildConfig.SDK_OVERSEA_NAME, BuildConfig.SDK_OVERSEA_VERSION);
        }

        return false;
    }

    @Override
    public boolean share(ShareRequest request) {
        return share(request.getShareRequest());
    }

    private boolean sendWebAuthRequest(Authorization.Request request) {
        return authImpl.authorizeWeb(TikTokWebAuthorizeActivity.class, request);

    }

    private IAPPCheckHelper getSupportApiAppInfo(int type) {

        switch (type) {
            case API_TYPE_LOGIN:
                for (IAPPCheckHelper checkapi : mAuthcheckApis) {
                    if (checkapi.isAppSupportAuthorization()) {
                        return checkapi;
                    }
                }
                break;
            case API_TYPE_SHARE:
                for (IAPPCheckHelper checkapi : mSharecheckApis) {
                    if (checkapi.isAppSupportShare()) {
                        return checkapi;
                    }
                }
                break;
        }

        return null;
    }

}
