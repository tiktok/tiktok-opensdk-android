package com.bytedance.sdk.open.aweme.common.impl;

import android.content.Context;
import android.text.TextUtils;


import com.bytedance.sdk.open.aweme.common.handler.BDDataHandler;
import com.bytedance.sdk.open.aweme.common.api.BDOpenApi;

import java.util.List;

/**
 * 创建 BDOpenApi
 * Created by yangzhirong on 2018/10/8.
 */
public class BDOpenAPiFactory {

    public static BDOpenApi create(Context context, BDOpenConfig config) {
        return create(context, config, null);
    }

    public static BDOpenApi create(Context context, BDOpenConfig config, List<BDDataHandler> handlerList) {
        if (config == null || TextUtils.isEmpty(config.clientKey)) {
            throw new IllegalStateException("no init client key");
        }
        return new BDOpenApiImpl(context, config, handlerList);
    }
}
