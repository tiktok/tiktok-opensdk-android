package com.bytedance.sdk.open.tiktok.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.base.IAPPCheckHelper;
import com.bytedance.sdk.open.aweme.common.constants.ParamKeyConstants;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.handler.IDataHandler;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.aweme.share.ShareDataHandler;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.aweme.share.ShareRequest;
import com.bytedance.sdk.open.tiktok.BuildConfig;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.helper.MusicallyCheckHelperImpl;
import com.bytedance.sdk.open.tiktok.helper.TikTokCheckHelperImpl;

import java.util.HashMap;
import java.util.Map;

public class TikTokOpenApiImpl implements TikTokOpenApi {

    private Context mContext;
    private final IAPPCheckHelper[] mSharecheckApis;


    private Map<Integer, IDataHandler> handlerMap = new HashMap<>(2);


    private ShareImpl shareImpl;

    private static final int API_TYPE_SHARE = 1;

    private static final String LOCAL_ENTRY_ACTIVITY = "tiktokapi.TikTokEntryActivity";
    private static final String REMOTE_SHARE_ACTIVITY = "share.SystemShareActivity";

    private static final int TYPE_SHARE_HANDLER = 2;

    public TikTokOpenApiImpl(Context context, ShareImpl shareImpl) {
        this.mContext = context;
        this.shareImpl = shareImpl;
        handlerMap.put(TYPE_SHARE_HANDLER, new ShareDataHandler());


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

        int type = bundle.getInt(ParamKeyConstants.BaseParams.TYPE);
        if (type == 0) {
            type = bundle.getInt(ParamKeyConstants.ShareParams.TYPE);
        }
        switch (type) {
            case CommonConstants.ModeType.SHARE_CONTENT_TO_TT:
            case CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP:
                return handlerMap.get(TYPE_SHARE_HANDLER).handle(type, bundle, eventHandler);
            default:
                return handlerMap.get(TYPE_SHARE_HANDLER).handle(type, bundle, eventHandler);
        }
    }

    @Override
    public boolean isAppInstalled() {
        for (IAPPCheckHelper checkapi : mSharecheckApis) {
            if (checkapi.isAppInstalled()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean isAppSupportShare() {
        return getSupportApiAppInfo(API_TYPE_SHARE) != null;
    }

    @Override
    public boolean isShareSupportFileProvider() {
        for (IAPPCheckHelper checkapi : mSharecheckApis) {
            if (checkapi.isShareSupportFileProvider()) {
                return true;
            }
        }
        return false;
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

    private IAPPCheckHelper getSupportApiAppInfo(int type) {

        switch (type) {
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
