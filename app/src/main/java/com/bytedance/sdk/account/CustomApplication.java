package com.bytedance.sdk.account;

import android.app.Application;

import com.bytedance.sdk.account.bdopen.impl.BDOpenConfig;
import com.bytedance.sdk.account.open.aweme.impl.TTOpenApiFactory;

/**
 * 主要功能：自定义{@link Application}
 * since: 2018/12/25
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String clientkey = "f7938a4c95ff3108"; // 需要到开发者网站申请
        TTOpenApiFactory.init(new BDOpenConfig(clientkey));
    }
}
