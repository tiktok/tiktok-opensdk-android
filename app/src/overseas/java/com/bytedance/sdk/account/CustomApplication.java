package com.bytedance.sdk.account;

import android.app.Application;

import com.bytedance.sdk.open.aweme.OpenApiFactory;
import com.bytedance.sdk.open.aweme.TikTokOpenConfig;

/**
 * 主要功能：自定义{@link Application}
 * since: 2018/12/25
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String clientkey = BuildConfig.CLIENT_KEY; // 需要到开发者网站申请
//        String clientkey = "xxxx"; // 需要到开发者网站申请

        OpenApiFactory.init(new TikTokOpenConfig(clientkey));
    }
}
