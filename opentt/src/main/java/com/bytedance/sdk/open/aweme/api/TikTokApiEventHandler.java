package com.bytedance.sdk.open.aweme.api;
import android.content.Intent;

import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;

/**
 * 请求
 * Created by yangzhirong on 2018/9/26.
 *
 * @author yangzhirong@bytedance.com
 */
public interface TikTokApiEventHandler {

    /**
     * 请求数据
     *
     * @param req
     */
    void onReq(BaseReq req);

    /**
     * 结果数据
     *
     * @param resp
     */
    void onResp(BaseResp resp);

    /**
     * 非正常 intent
     *
     * @param intent
     */
    void onErrorIntent(Intent intent);
}
