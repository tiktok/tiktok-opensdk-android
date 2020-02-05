package com.bytedance.sdk.open.aweme.authorize.handler;

import android.os.Bundle;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.IDataHandler;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;

/**
 * data parse of authorization result
 * Created by yangzhirong on 2018/10/8.
 */
public class SendAuthDataHandler implements IDataHandler {
    @Override
    public boolean handle(int type, Bundle bundle, IApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == CommonConstants.ModeType.SEND_AUTH_REQUEST) {
            Authorization.Request request = new Authorization.Request(bundle);
            if (request.checkArgs()) {
                // deal with white space
                if (request.scope != null) {
                    request.scope = request.scope.replace(" ","");
                }
                if (request.optionalScope1 != null) {
                    request.optionalScope1 = request.optionalScope1.replace(" ", "");
                }
                if (request.optionalScope0 != null) {
                    request.optionalScope0 = request.optionalScope0.replace(" ", "");
                }
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == CommonConstants.ModeType.SEND_AUTH_RESPONSE) {
            Authorization.Response response = new Authorization.Response(bundle);
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
