package com.bytedance.sdk.open.tiktok;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.bytedance.sdk.open.tiktok.authorize.AuthService;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl;
import com.bytedance.sdk.open.tiktok.share.ShareService;

public class TikTokOpenApiFactory {

    private static TikTokOpenConfig sConfig;

    public static boolean init(TikTokOpenConfig config) {
        if (config != null && !TextUtils.isEmpty(config.getClientKey())) {
            sConfig = config;
            return true;
        }
        return false;
    }
    private static TikTokOpenApiImpl sharedApiImpl;
    public static TikTokOpenApi create(Activity activity) {
        return create(activity, null);
    }

    public static TikTokOpenApi create(Activity activity, @Nullable IApiEventHandler handler) {
        if (sConfig == null) {
            return null;
        }
        synchronized (TikTokOpenApiFactory.class) {
            if (sharedApiImpl == null) {
                ShareService share = new ShareService(activity, sConfig.getClientKey());
                AuthService authService = new AuthService(activity, sConfig.getClientKey());
                sharedApiImpl = new TikTokOpenApiImpl(activity, authService, share, handler);
            }
            return sharedApiImpl;
        }
    }
}
