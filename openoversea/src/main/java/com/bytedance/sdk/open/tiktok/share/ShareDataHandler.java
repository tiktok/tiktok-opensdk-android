package com.bytedance.sdk.open.tiktok.share;

import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.CommonConstants;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler;

/**
 * sharing data parser
 */
public class ShareDataHandler implements IDataHandler {
    @Override
    public boolean handle(int type, Bundle bundle, IApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == CommonConstants.ModeType.SHARE_CONTENT_TO_TT) {
            Share.Request request = new Share.Request(bundle);
            if (request.checkArgs()) {
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == CommonConstants.ModeType.SHARE_CONTENT_TO_TT_RESP) {
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
