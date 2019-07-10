package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.bytedance.sdk.open.aweme.AwemeCheckHelperImpl;
import com.bytedance.sdk.open.aweme.api.DYOpenApi;
import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.authorize.handler.SendAuthDataHandler;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.base.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.constants.DYOpenConstants;
import com.bytedance.sdk.open.aweme.common.handler.BDApiEventHandler;
import com.bytedance.sdk.open.aweme.common.handler.BDDataHandler;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.aweme.share.ShareDataHandler;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.aweme.utils.AppUtil;
import com.bytedance.sdk.open.aweme.utils.SignatureUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangzhirong on 2018/10/17.
 * 此类对应抖音版本;
 */
class DYOpenApiImpl implements DYOpenApi {

    public static final String WAP_AUTHORIZE_URL = "wap_authorize_url";

    private Context mContext;
    private ShareImpl shareImpl;
    private AuthImpl authImpl;
    private Map<Integer, BDDataHandler> handlerMap = new HashMap<>(2);
    private static final int TYPE_AUTH_HANDLER = 1;
    private static final int TYPE_SHARE_HANDLER = 2;


    /**
     * 以下参数  需要改成提供授权方App的
     * 此处为头条App的
     */
    static final String VALIDATE_SIGNATURE = "aea615ab910015038f73c47e45d21466"; // 授权方签名校验码
    static final String REMOTE_ENTRY_PACKAGE = "com.ss.android.ugc.aweme"; // 授权方包名
    static final String REMOTE_ENTRY_ACTIVITY = "openauthorize.AwemeAuthorizedActivity"; // 提供授权的Activity入口
    static final String LOCAL_ENTRY_ACTIVITY = "bdopen.BdEntryActivity"; // 请求授权的结果回调Activity入口
    static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity"; // 分享的Activity入口

    DYOpenApiImpl(Context context, AuthImpl auth, ShareImpl shareImpl) {
        this.mContext = context;
        this.authImpl = auth;
        this.shareImpl = shareImpl;
        handlerMap.put(TYPE_AUTH_HANDLER, new SendAuthDataHandler());
        handlerMap.put(TYPE_SHARE_HANDLER, new ShareDataHandler());
    }

    @Override
    public boolean handleIntent(Intent intent, BDApiEventHandler eventHandler) {
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

        int type = bundle.getInt(BDOpenConstants.Params.TYPE) == 0 ? bundle.getInt(DYOpenConstants.Params.TYPE) : 0;
        switch (type) {
            case BDOpenConstants.ModeType.SEND_AUTH_REQUEST:
            case BDOpenConstants.ModeType.SEND_AUTH_RESPONSE:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
            case DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY:
            case DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY_RESP:
                return handlerMap.get(TYPE_SHARE_HANDLER).handle(type, bundle, eventHandler);
            default:
                return handlerMap.get(TYPE_AUTH_HANDLER).handle(type, bundle, eventHandler);
        }
    }

    @Override public boolean isAppSupportAuthorization(int targetApp) {
        return new AwemeCheckHelperImpl(mContext).isAppSupportAuthorization();
    }

    @Override public boolean isAppSupportShare(int targetApp) {
        return new AwemeCheckHelperImpl(mContext).isAppSupportShare();
    }

    @Override @Deprecated
    public boolean isAppInstalled() {
        return AppUtil.isAppInstalled(mContext, REMOTE_ENTRY_PACKAGE);
    }

    @Override
    public boolean validateSign() {
        return SignatureUtils.validateSign(mContext, REMOTE_ENTRY_PACKAGE, VALIDATE_SIGNATURE);
    }

    @Override
    public boolean sendAuthLogin(Authorization.Request request) {
        IAPPCheckHelper appHasInstalled;
        if (request.targetApp == DYOpenConstants.TARGET_APP.AWEME) {
            appHasInstalled = new AwemeCheckHelperImpl(mContext);
            if (!appHasInstalled.isAppSupportAuthorization()) {
                // 这个时候抖音没安装所以要走web授权
                appHasInstalled = null;
            }
        } else {
            throw new IllegalArgumentException("We only support AWEME And TIKTOK for authorization.");
        }
        if (appHasInstalled != null && authImpl.authorizeNative(request, appHasInstalled.getPackageName(), appHasInstalled.getRemoteAuthEntryActivity(), LOCAL_ENTRY_ACTIVITY)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean share(Share.Request request) {
        if (isAppInstalled() && isAppSupportShare(request.mTargetApp)) {
            return shareImpl.share(LOCAL_ENTRY_ACTIVITY, REMOTE_ENTRY_PACKAGE, REMOTE_SHARE_ACTIVITY, request);
        } else {
            return false;
        }
    }

}
