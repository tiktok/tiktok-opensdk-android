package com.bytedance.sdk.open.tiktok.share;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bytedance.sdk.open.tiktok.common.constants.Constants;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler;

/**
 * sharing data parser
 */
public class ShareDataHandler implements IDataHandler {
    @Override
    public boolean handle(int type, @Nullable Bundle bundle, @Nullable IApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == Constants.TIKTOK.SHARE_REQUEST) {
            Share.Request request = new Share.Request();
            request.fromBundle(bundle);
            if (request.validate()) {
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == Constants.TIKTOK.SHARE_RESPONSE) {
            Share.Response response = new Share.Response();
            response.fromBundle(bundle);
            if (response.validate()) {
                eventHandler.onResp(response);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
