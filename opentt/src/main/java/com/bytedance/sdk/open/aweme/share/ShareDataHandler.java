package com.bytedance.sdk.open.aweme.share;

import android.os.Bundle;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.api.BDDataHandler;
import com.bytedance.sdk.open.aweme.DYOpenConstants;

/**
 * share 请求/结果的 数据解析
 */
public class ShareDataHandler implements BDDataHandler {
    @Override
    public boolean handle(int type, Bundle bundle, BDApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY) {
            Share.Request request = new Share.Request(bundle);
            if (request.checkArgs()) {
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY_RESP) {
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
