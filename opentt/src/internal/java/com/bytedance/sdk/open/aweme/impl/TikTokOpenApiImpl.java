package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bytedance.sdk.account.open.aweme.BuildConfig;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.base.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.authorize.handler.SendAuthDataHandler;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.handler.IDataHandler;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.aweme.share.ShareDataHandler;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
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
    private int mTargetApp;

    static final int API_TYPE_LOGIN = 0;
    static final int API_TYPE_SHARE = 1;

    static final String LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity"; // 请求授权的结果回调Activity入口
    static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口

    public static final String WAP_AUTHORIZE_URL = "wap_authorize_url";

    private static final int TYPE_AUTH_HANDLER = 1;
    private static final int TYPE_SHARE_HANDLER = 2;

    public TikTokOpenApiImpl(Context context, AuthImpl authImpl, ShareImpl shareImpl,int targetApp) {
        this.mContext = context;
        this.shareImpl = shareImpl;
        this.authImpl = authImpl;
        handlerMap.put(TYPE_AUTH_HANDLER, new SendAuthDataHandler());
        handlerMap.put(TYPE_SHARE_HANDLER, new ShareDataHandler());
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
    public String getSdkVersion() {
        return BuildConfig.SDK_VERSION;
    }

    @Override
    public boolean authorizeNative(Authorization.Request request) {  //这个地方注意原来的逻辑，最后看看怎么恢复
        IAPPCheckHelper appHasInstalled = null;
        if (mTargetApp == DouYinConstants.TARGET_APP.AWEME) {
            appHasInstalled = new AwemeCheckHelperImpl(mContext);
            if (!appHasInstalled.isAppSupportAuthorization()) {
                // 这个时候抖音没安装所以要走web授权
                appHasInstalled = null;
            }
        }
        if (appHasInstalled != null && authImpl.authorizeNative(request, appHasInstalled.getPackageName(), appHasInstalled.getRemoteAuthEntryActivity(), LOCAL_ENTRY_ACTIVITY)) {
            return true;
        } else {
            return sendWebAuthRequest(request);
        }
    }

    @Override
    public boolean authorize(Authorization.Request request) {
      return authorizeNative(request);
    }

    @Override
    public boolean authorizeWeb(Authorization.Request request) {
        return sendWebAuthRequest(request);
    }

    @Override
    public boolean authorizeWeb(Authorization.Request request, Class cla) {
        return senWebAuthRequest(request, cla);
    }

    @Override public boolean isAppSupportAuthorization() {
        if (mTargetApp == DouYinConstants.TARGET_APP.AWEME) {
            return new AwemeCheckHelperImpl(mContext).isAppSupportAuthorization();
        }
        return false;
    }

    @Override
    public boolean isAppSupportShare() {
        if (mTargetApp == DouYinConstants.TARGET_APP.AWEME) {
            return new AwemeCheckHelperImpl(mContext).isAppSupportShare();
        }
        return false;
    }

    /**
     * 应部分厂商需求打开此api. 不太建议使用
     *
     * 就算安装了，版本不支持，功能一样不可以使用，可以直接用功能判断接口;
     * @return
     */
    @Override
    public boolean isAppInstalled() {
        if (mTargetApp == DouYinConstants.TARGET_APP.AWEME) {
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

    @Override
    public String getWapUrlIfAuthByWap(Authorization.Response response) {
        // 该数据是在 wap授权页面sendInnerResponse方法添加的。
        if (response != null && response.extras != null && response.extras.containsKey(WAP_AUTHORIZE_URL)) {
            return response.extras.getString(WAP_AUTHORIZE_URL, "");
        }
        return null;
    }

    private boolean sendWebAuthRequest(Authorization.Request request) {
      if (mTargetApp == DouYinConstants.TARGET_APP.AWEME) {
            return authImpl.authorizeWeb(AwemeWebAuthorizeActivity.class, request);
        } else {
            throw new IllegalArgumentException("We only support DOUYIN for authorization.");
        }
    }

    private boolean senWebAuthRequest(Authorization.Request request, Class cla) {
      if (mTargetApp == DouYinConstants.TARGET_APP.AWEME) {
            return authImpl.authorizeWeb(AwemeWebAuthorizeActivity.class, request);
        } else {
            throw new IllegalArgumentException("We only support DOUYIN for authorization.");
        }
    }
}
