package com.bytedance.sdk.open.aweme.share;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.api.TikTokApiEventHandler;
import com.bytedance.sdk.open.aweme.TikTokConstants;
import com.bytedance.sdk.open.aweme.common.handler.TikTokDataHandler;

/**
 * sharing data parser
 */
public class ShareDataHandler implements TikTokDataHandler {
    @Override
    public boolean handle(int type, Bundle bundle, TikTokApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == TikTokConstants.ModeType.SHARE_CONTENT_TO_TT) {
            Share.Request request = new Share.Request(bundle);
            if (request.checkArgs()) {
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == TikTokConstants.ModeType.SHARE_CONTENT_TO_TT_RESP) {
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
