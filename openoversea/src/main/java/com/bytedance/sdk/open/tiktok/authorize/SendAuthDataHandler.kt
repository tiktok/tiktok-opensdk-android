package com.bytedance.sdk.open.tiktok.authorize

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler

class SendAuthDataHandler : IDataHandler {
    override fun handle(type: Int, bundle: Bundle, eventHandler: IApiEventHandler): Boolean {
        return when (type) {
            Constants.TIKTOK.AUTH_REQUEST -> {
                val request = Auth.Request()
                request.fromBundle(bundle)
                if (request.validate()) {
                    // deal with white space
                    request.scope = request.scope?.replace(" ", "")
                    request.optionalScope1 = request.optionalScope1?.replace(" ", "")
                    request.optionalScope0 = request.optionalScope0?.replace(" ", "")
                    eventHandler.onRequest(request)
                    true
                } else {
                    false
                }
            }
            Constants.TIKTOK.AUTH_RESPONSE -> {
                val response = Auth.Response()
                response.fromBundle(bundle)
                if (response.validate()) {
                    eventHandler.onResponse(response)
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }
}
