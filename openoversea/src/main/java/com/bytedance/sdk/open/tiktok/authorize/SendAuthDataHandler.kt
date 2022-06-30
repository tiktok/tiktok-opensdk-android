package com.bytedance.sdk.open.tiktok.authorize

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler

class SendAuthDataHandler: IDataHandler {
    override fun handle(type: Int, bundle: Bundle?, eventHandler: IApiEventHandler?): Boolean {
        if (bundle == null || eventHandler == null) {
            return false
        }
        if (type == Constants.TIKTOK.AUTH_REQUEST) {
            val request = Auth.Request()
            request.fromBundle(bundle)
            return if (request.validate()) {
                // deal with white space
                if (request.scope != null) {
                    request.scope = request.scope!!.replace(" ", "")
                }
                if (request.optionalScope1 != null) {
                    request.optionalScope1 = request.optionalScope1!!.replace(" ", "")
                }
                if (request.optionalScope0 != null) {
                    request.optionalScope0 = request.optionalScope0!!.replace(" ", "")
                }
                eventHandler.onReq(request)
                true
            } else {
                false
            }
        } else if (type == Constants.TIKTOK.AUTH_RESPONSE) {
            val response = Auth.Response()
            response.fromBundle(bundle)
            return if (response.validate()) {
                eventHandler.onResp(response)
                true
            } else {
                false
            }
        }
        return false
    }
}