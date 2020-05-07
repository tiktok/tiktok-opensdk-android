package com.bytedance.sdk.open.douyin;

import android.app.Activity;
import android.text.TextUtils;

import com.bytedance.sdk.open.aweme.authorize.AuthImpl;
import com.bytedance.sdk.open.aweme.share.ShareImpl;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;
import com.bytedance.sdk.open.douyin.impl.DouYinOpenApiImpl;

/**
 * TTOpenApi
 *
 * @author changlei@bytedance.com
 */
public class DouYinOpenApiFactory {

    private static DouYinOpenConfig sConfig;

    public static boolean init(DouYinOpenConfig config) {
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
    public static DouYinOpenApi create(Activity activity) {
        AuthImpl auth = new AuthImpl(activity, sConfig.clientKey);
        ShareImpl share = new ShareImpl(activity, sConfig.clientKey);
        ShareToContactImpl shareToContact = new ShareToContactImpl(activity, sConfig.clientKey);
        return new DouYinOpenApiImpl(activity, auth, share, shareToContact);
    }


}
