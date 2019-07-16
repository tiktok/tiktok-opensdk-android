package com.bytedance.sdk.open.aweme.common.handler;

import android.os.Bundle;



/**
 * Bundle 数据解析
 * Created by yangzhirong on 2018/10/8.
 */
public interface TikTokDataHandler {

    /**
     * @param type         类型( Request/Response
     * @param bundle       数据
     * @param eventHandler 接收数据解析结果的回调
     * @return 是否解析改 type
     */
    boolean handle(int type, Bundle bundle, TikTokApiEventHandler eventHandler);
}
