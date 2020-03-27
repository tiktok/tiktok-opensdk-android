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
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.handler.IDataHandler;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.aweme.share.ShareDataHandler;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.douyin.BuildConfig;
import com.bytedance.sdk.open.douyin.ShareToContact;
import com.bytedance.sdk.open.douyin.ShareToContactImpl;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.open.douyin.ui.DouYinWebAuthorizeActivity;

import java.util.HashMap;
import java.util.Map;

public class DouYinOpenApiImpl implements DouYinOpenApi {

    private Context mContext;


    private Map<Integer, IDataHandler> handlerMap = new HashMap<>(2);


    private ShareImpl shareImpl;
    private AuthImpl authImpl;
    private ShareToContactImpl contactImpl;

    private static final String LOCAL_ENTRY_ACTIVITY = "douyinapi.DouYinEntryActivity"; // 请求授权的结果回调Activity入口
    private static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口

    public static final String WAP_AUTHORIZE_URL = "wap_authorize_url";



    private static final int TYPE_AUTH_HANDLER = 1;
    private static final int TYPE_SHARE_HANDLER = 2;

    public DouYinOpenApiImpl(Context context, AuthImpl authImpl, ShareImpl shareImpl, ShareToContactImpl contactImpl) {
        this.mContext = context;
        this.shareImpl = shareImpl;
        this.authImpl = authImpl;
        this.contactImpl = contactImpl;
        handlerMap.put(TYPE_AUTH_HANDLER, new SendAuthDataHandler());
        handlerMap.put(TYPE_SHARE_HANDLER, new ShareDataHandler());

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
    public boolean isAppInstalled() {
        return new DouYinCheckHelperImpl(mContext).isAppInstalled();
    }

    @Override
    public String getWapUrlIfAuthByWap(Authorization.Response response) {
        // 该数据是在 wap授权页面sendInnerResponse方法添加的。
        if (response != null && response.extras != null && response.extras.containsKey(WAP_AUTHORIZE_URL)) {
            return response.extras.getString(WAP_AUTHORIZE_URL, "");
        }
        return null;
    }

    @Override
    public String getSdkVersion() {
        return BuildConfig.SDK_CHINA_VERSION;
    }

    @Override
    public boolean shareToContacts(ShareToContact.Request request) {
        DouYinCheckHelperImpl checkHelper = new DouYinCheckHelperImpl(mContext);
        if (checkHelper.isSupportShareToContact()) {
            contactImpl.shareToContacts(LOCAL_ENTRY_ACTIVITY,
                    checkHelper.getPackageName(),
                    "openshare.ShareToContactsActivity", request);
            return true;
        }
        return false;

    }

    @Override
    public boolean isAppSupportAuthorization() {
        return new DouYinCheckHelperImpl(mContext).isAppSupportAuthorization();

    }

    @Override
    public boolean isAppSupportShare() {
        return new DouYinCheckHelperImpl(mContext).isAppSupportShare();

    }

    @Override
    public boolean authorize(Authorization.Request request) {
        if (request == null) {
            return false;
        }
        IAPPCheckHelper appHasInstalled = new DouYinCheckHelperImpl(mContext);
        if (appHasInstalled.isAppSupportAuthorization()) {
            return authImpl.authorizeNative(request, appHasInstalled.getPackageName(), appHasInstalled.getRemoteAuthEntryActivity(), LOCAL_ENTRY_ACTIVITY, BuildConfig.SDK_CHINA_NAME, BuildConfig.SDK_CHINA_VERSION);
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
        DouYinCheckHelperImpl checkHelper = new DouYinCheckHelperImpl(mContext);
        if (mContext != null && checkHelper.isAppSupportShare()) {
            return shareImpl.share(LOCAL_ENTRY_ACTIVITY, checkHelper.getPackageName(), REMOTE_SHARE_ACTIVITY, request,
                    checkHelper.getRemoteAuthEntryActivity(), BuildConfig.SDK_CHINA_NAME, BuildConfig.SDK_CHINA_VERSION);
        }
        return false;
    }

    private boolean sendWebAuthRequest(Authorization.Request request) {
        return authImpl.authorizeWeb(DouYinWebAuthorizeActivity.class, request);

    }

}
