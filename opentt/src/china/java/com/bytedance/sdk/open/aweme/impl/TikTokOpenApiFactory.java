package com.bytedance.sdk.open.aweme.impl;

import android.content.Context;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.common.constants.TikTokConstants;
import com.bytedance.sdk.open.aweme.common.impl.TikTokOpenConfig;
import com.bytedance.sdk.open.aweme.share.ShareImpl;

/**
 * TTOpenApi
 *
 * @author changlei@bytedance.com
 */
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
     * @param context
     * @return
     */
    public static TiktokOpenApi create(Context context) {
        AuthImpl auth = new AuthImpl(context, sConfig);
        ShareImpl share = new ShareImpl(context, sConfig);
        return new TikTokOpenApiImpl(context, auth, share, TikTokConstants.TARGET_APP.TIKTOK);
    }

    /**
     * 创建 TTOpenApi
     *
     * @param context
     * @return
     */
    public static TiktokOpenApi create(Context context, int targetApp) {
        AuthImpl auth = new AuthImpl(context, sConfig);
        ShareImpl share = new ShareImpl(context, sConfig);
        return new TikTokOpenApiImpl(context, auth, share, targetApp);
    }


}
