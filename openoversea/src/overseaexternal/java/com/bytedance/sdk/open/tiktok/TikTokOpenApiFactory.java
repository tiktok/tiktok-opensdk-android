package com.bytedance.sdk.open.tiktok;

import android.app.Activity;
import android.text.TextUtils;

import com.bytedance.sdk.open.tiktok.authorize.AuthService;
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
    /**
     * 创建 TTOpenApi
     *
     * @param activity
     * @return
     */
    public static TikTokOpenApi create(Activity activity) {
        if (sConfig == null) {
            return null;
        }
        ShareImpl share = new ShareImpl(activity, sConfig.clientKey);
        AuthService auth = new AuthService(activity, sConfig.clientKey);
        return new TikTokOpenApiImpl(activity, auth, share);
    }
}
