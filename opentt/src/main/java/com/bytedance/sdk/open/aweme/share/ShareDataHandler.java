package com.bytedance.sdk.open.aweme.share;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.common.constants.BDOpenConstants;
import com.bytedance.sdk.open.aweme.common.constants.TikTokConstants;
import com.bytedance.sdk.open.aweme.common.handler.BDApiEventHandler;
import com.bytedance.sdk.open.aweme.common.handler.BDDataHandler;

/**
 * share 请求/结果的 数据解析
 */
public class ShareDataHandler implements BDDataHandler {
    @Override
    public boolean handle(int type, Bundle bundle, BDApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == TikTokConstants.ModeType.SHARE_CONTENT_TO_DY) {
            Share.Request request = new Share.Request(bundle);
            if (request.checkArgs()) {
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == TikTokConstants.ModeType.SHARE_CONTENT_TO_DY_RESP) {
            Share.Response response = new Share.Response(bundle);
            if (response.checkArgs()) {
                eventHandler.onResp(response);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
