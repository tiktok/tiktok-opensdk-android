package com.bytedance.sdk.open.aweme.authorize;

import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.open.aweme.DYOpenConstants;

/**
 * 主要功能：授权相关
 * author: ChangLei
 * since: 2019/5/17
 */
public class Authorization {

    /**
     * Tiktok Authorization Request
     */
    public static class Request extends SendAuth.Request {

        public int targetApp = DYOpenConstants.TARGET_APP.TIKTOK; //默认tiktok
    }
}
