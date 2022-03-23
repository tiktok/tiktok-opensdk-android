package com.bytedance.sdk.open.tiktok;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.bytedance.sdk.open.tiktok.authorize.AuthImpl;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.share.ShareImpl;
import com.bytedance.sdk.open.tiktok.api.TikTokOpenApi;
import com.bytedance.sdk.open.tiktok.impl.TikTokOpenApiImpl;

public class TikTokOpenApiFactory {

    private static TikTokOpenConfig sConfig;

    public static boolean init(TikTokOpenConfig config) {
        if (config != null && !TextUtils.isEmpty(config.clientKey)) {
            sConfig = config;
            return true;
        }
        return false;
    }
    private static TikTokOpenApiImpl sharedApiImpl;
    public static TikTokOpenApi create(Activity activity) {
        return create(activity, null);
    }
    /**
     * 创建 TTOpenApi
     *
     * @param activity
     * @return
     */
    public static TikTokOpenApi create(Activity activity, @Nullable IApiEventHandler handler) {
        if (sConfig == null) {
            return null;
        }
        synchronized (TikTokOpenApiFactory.class) {
            if (sharedApiImpl == null) {
                ShareImpl share = new ShareImpl(activity, sConfig.clientKey);
                AuthImpl auth = new AuthImpl(activity, sConfig.clientKey);
                sharedApiImpl = new TikTokOpenApiImpl(activity, auth, share, handler);
            }
            return sharedApiImpl;
        }
//        AuthImpl auth = new AuthImpl(activity, sConfig.clientKey);
//        ShareImpl share = new ShareImpl(activity, sConfig.clientKey);
//        return new TikTokOpenApiImpl(activity,auth, share);
    }


}
