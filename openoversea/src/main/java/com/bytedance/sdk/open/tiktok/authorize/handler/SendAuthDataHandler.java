package com.bytedance.sdk.open.tiktok.authorize.handler;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bytedance.sdk.open.tiktok.authorize.model.Auth;
import com.bytedance.sdk.open.tiktok.common.constants.Constants;
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler;

/**
 * data parse of authorization result
 * Created by yangzhirong on 2018/10/8.
 */
public class SendAuthDataHandler implements IDataHandler {
    @Override
    public boolean handle(int type, @Nullable  Bundle bundle, @Nullable IApiEventHandler eventHandler) {
        if (bundle == null || eventHandler == null) {
            return false;
        }
        if (type == Constants.TIKTOK.AUTH_REQUEST) {
            Auth.Request request = new Auth.Request();
            request.fromBundle(bundle);
            if (request.validate()) {
                // deal with white space
                if (request.getScope() != null) {
                    request.setScope(request.getScope().replace(" ",""));
                }
                if (request.getOptionalScope1() != null) {
                    request.setOptionalScope1(request.getOptionalScope1().replace(" ", ""));
                }
                if (request.getOptionalScope0() != null) {
                    request.setOptionalScope0(request.getOptionalScope0().replace(" ", ""));
                }
                eventHandler.onReq(request);
                return true;
            } else {
                return false;
            }
        } else if (type == Constants.TIKTOK.AUTH_RESPONSE) {
            Auth.Response response = new Auth.Response();
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
