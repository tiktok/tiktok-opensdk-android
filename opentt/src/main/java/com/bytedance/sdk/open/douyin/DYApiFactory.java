package com.bytedance.sdk.open.douyin;

import android.content.Context;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.douyin.api.DYOpenApi;
import com.bytedance.sdk.open.douyin.impl.DYOpenApiImpl;

/**
 * TTOpenApi
 *
 * @author changlei@bytedance.com
 */
public class DYApiFactory {

    private static DYOpenConfig sConfig;

    public static boolean init(DYOpenConfig config) {
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
    public static DYOpenApi create(Context context) {
        AuthImpl auth = new AuthImpl(context, sConfig.clientKey);
        ShareImpl share = new ShareImpl(context, sConfig.clientKey);
        return new DYOpenApiImpl(context, auth, share);
    }


}
