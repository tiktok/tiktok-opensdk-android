package com.bytedance.sdk.open.tiktok.share;

import android.os.Bundle;

import com.bytedance.sdk.open.tiktok.common.constants.Constants;
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
        if (type == Constants.TIKTOK.SHARE_REQUEST) {
            Share.Request request = new Share.Request(bundle);
            if (request.checkArgs()) {
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == Constants.TIKTOK.SHARE_RESPONSE) {
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
