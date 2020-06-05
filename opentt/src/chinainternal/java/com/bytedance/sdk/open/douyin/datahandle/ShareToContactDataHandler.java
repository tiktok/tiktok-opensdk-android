package com.bytedance.sdk.open.douyin.datahandle;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.handler.IDataHandler;
import com.bytedance.sdk.open.aweme.share.Share;
import com.bytedance.sdk.open.douyin.ShareToContact;

public class ShareToContactDataHandler implements IDataHandler {
    @Override
    public boolean handle(int type, Bundle bundle, IApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == CommonConstants.ModeType.SHARE_TO_CONTACTS) {
            ShareToContact.Request request = new ShareToContact.Request(bundle);
            if (request.checkArgs()) {
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == CommonConstants.ModeType.SHARE_TO_CONTACT_RESP) {
            ShareToContact.Response response = new ShareToContact.Response(bundle);
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
