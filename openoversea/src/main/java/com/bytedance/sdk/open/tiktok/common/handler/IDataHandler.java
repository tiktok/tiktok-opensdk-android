package com.bytedance.sdk.open.tiktok.common.handler;

import android.os.Bundle;


public interface IDataHandler {

    /**
     * @param type         type of Request/Response
     * @param bundle       data
     * @param eventHandler handle return data
     * @return if it can parse data
     */
    boolean handle(int type, Bundle bundle, IApiEventHandler eventHandler);
}
