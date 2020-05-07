package com.bytedance.sdk.open.tiktok;

import android.app.Activity;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
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

    /**
     * 创建 TTOpenApi
     *
     * @param activity
     * @return
     */
    public static TikTokOpenApi create(Activity activity) {
        AuthImpl auth = new AuthImpl(activity, sConfig.clientKey);
        ShareImpl share = new ShareImpl(activity, sConfig.clientKey);
        return new TikTokOpenApiImpl(activity,auth, share);
    }


}
