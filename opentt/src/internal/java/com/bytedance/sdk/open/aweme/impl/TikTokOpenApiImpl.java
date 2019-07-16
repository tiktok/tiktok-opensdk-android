package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bytedance.sdk.open.aweme.authorize.ui.AwemeWebAuthorizeActivity;
import com.bytedance.sdk.open.aweme.authorize.ui.TikTokWebAuthorizeActivity;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.base.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.authorize.handler.SendAuthDataHandler;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.constants.TikTokConstants;
import com.bytedance.sdk.open.aweme.common.handler.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.common.handler.TikTokDataHandler;
import com.bytedance.sdk.open.aweme.common.impl.AwemeCheckHelperImpl;
import com.bytedance.sdk.open.aweme.common.impl.MusicallyCheckHelperImpl;
import com.bytedance.sdk.open.aweme.common.impl.TiktokCheckHelperImpl;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.aweme.share.ShareDataHandler;
import com.bytedance.sdk.open.aweme.share.ShareImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiktok授权实现类
 *
 * @author changlei@bytedance.com
 */

public class TikTokOpenApiImpl implements TiktokOpenApi {

    private Context mContext;

    private final IAPPCheckHelper[] mAuthcheckApis;
    private final IAPPCheckHelper[] mSharecheckApis;

    private Map<Integer, TikTokDataHandler> handlerMap = new HashMap<>(2);


    private ShareImpl shareImpl;
    private AuthImpl authImpl;
    private int mTargetApp;

    static final int API_TYPE_LOGIN = 0;
    static final int API_TYPE_SHARE = 1;

    static final String LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity"; // 请求授权的结果回调Activity入口
    static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口

    public static final String WAP_AUTHORIZE_URL = "wap_authorize_url";

    private static final int TYPE_AUTH_HANDLER = 1;
    private static final int TYPE_SHARE_HANDLER = 2;

    TikTokOpenApiImpl(Context context, AuthImpl authImpl, ShareImpl shareImpl,int targetApp) {
        this.mContext = context;
        this.shareImpl = shareImpl;
        this.authImpl = authImpl;
        handlerMap.put(TYPE_AUTH_HANDLER, new SendAuthDataHandler());
        handlerMap.put(TYPE_SHARE_HANDLER, new ShareDataHandler());
        mAuthcheckApis = new IAPPCheckHelper[] {
                new MusicallyCheckHelperImpl(context),
                new TiktokCheckHelperImpl(context)
        };

        mSharecheckApis = new IAPPCheckHelper[] {
                new MusicallyCheckHelperImpl(context),
                new TiktokCheckHelperImpl(context)
        };
        mTargetApp = targetApp;
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
            case TikTokConstants.ModeType.SEND_AUTH_REQUEST:
            case TikTokConstants.ModeType.SEND_AUTH_RESPONSE:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
            case TikTokConstants.ModeType.SHARE_CONTENT_TO_DY:
            case TikTokConstants.ModeType.SHARE_CONTENT_TO_DY_RESP:
                return handlerMap.get(TYPE_SHARE_HANDLER).handle(type, bundle, eventHandler);
            default:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
        }
    }

    @Override public boolean isAppSupportAuthorization() {
        if (mTargetApp == TikTokConstants.TARGET_APP.AWEME) {
            return new AwemeCheckHelperImpl(mContext).isAppSupportAuthorization();
        } else {
            return getSupportApiAppInfo(API_TYPE_LOGIN) != null;
        }
    }

    @Override
    public boolean isAppSupportShare() {
        if (mTargetApp == TikTokConstants.TARGET_APP.AWEME) {
            return new AwemeCheckHelperImpl(mContext).isAppSupportShare();
        } else {
            return getSupportApiAppInfo(API_TYPE_SHARE) != null;
        }
    }

    /**
     * 应部分厂商需求打开此api. 不太建议使用
     *
     * 就算安装了，版本不支持，功能一样不可以使用，可以直接用功能判断接口;
     * @return
     */
    @Override
    public boolean isAppInstalled() {
        if (mTargetApp == TikTokConstants.TARGET_APP.AWEME) {
            return new AwemeCheckHelperImpl(mContext).isAppInstalled();
        } else {
            for (IAPPCheckHelper checkapi : mAuthcheckApis) {
                if (checkapi.isAppInstalled()) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean authorize(Authorization.Request request) {
        IAPPCheckHelper appHasInstalled;
        if (mTargetApp == TikTokConstants.TARGET_APP.AWEME) {
            appHasInstalled = new AwemeCheckHelperImpl(mContext);
            if (!appHasInstalled.isAppSupportAuthorization()) {
                // 这个时候抖音没安装所以要走web授权
                appHasInstalled = null;
            }
        } else if (mTargetApp == TikTokConstants.TARGET_APP.TIKTOK) {
            appHasInstalled = getSupportApiAppInfo(API_TYPE_LOGIN);
        } else {
            throw new IllegalArgumentException("We only support AWEME And TIKTOK for authorization.");
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
        if (mTargetApp == TikTokConstants.TARGET_APP.AWEME) {
            AwemeCheckHelperImpl checkHelper = new AwemeCheckHelperImpl(mContext);
            if (mContext != null && checkHelper.isAppSupportShare()) {
                return shareImpl.share(LOCAL_ENTRY_ACTIVITY, checkHelper.getPackageName(), REMOTE_SHARE_ACTIVITY, request,
                        checkHelper.getRemoteAuthEntryActivity());
            }
        } else {
            // MT需要判断用户安装了哪个，并且哪个支持分享功能
            if (isAppSupportShare()) {
                String remotePackage = getSupportApiAppInfo(API_TYPE_SHARE).getPackageName();// 授权方包名
                return shareImpl.share(LOCAL_ENTRY_ACTIVITY, remotePackage, REMOTE_SHARE_ACTIVITY, request,
                        getSupportApiAppInfo(API_TYPE_SHARE).getRemoteAuthEntryActivity());
            }
        }

        return false;
    }

    @Nullable
    @Override
    public String getWapUrlIfAuthByWap(Authorization.Response response) {
        // 该数据是在 wap授权页面sendInnerResponse方法添加的。
        if (response != null && response.extras != null && response.extras.containsKey(WAP_AUTHORIZE_URL)) {
            return response.extras.getString(WAP_AUTHORIZE_URL, "");
        }
        return null;
    }

    private boolean sendWebAuthRequest(Authorization.Request request) {
        if (mTargetApp == TikTokConstants.TARGET_APP.TIKTOK) {
            return authImpl.authorizeWeb(TikTokWebAuthorizeActivity.class, request);
        } else if (mTargetApp == TikTokConstants.TARGET_APP.AWEME) {
            return authImpl.authorizeWeb(AwemeWebAuthorizeActivity.class, request);
        } else {
            throw new IllegalArgumentException("We only support AWEME And TIKTOK for authorization.");
        }
    }


    @Nullable
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
