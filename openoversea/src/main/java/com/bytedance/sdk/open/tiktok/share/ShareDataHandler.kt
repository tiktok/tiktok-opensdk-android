package com.bytedance.sdk.open.tiktok.share

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler

class ShareDataHandler : IDataHandler {
    override fun handle(type: Int, bundle: Bundle, eventHandler: IApiEventHandler): Boolean {
        return when (type) {
            Constants.TIKTOK.SHARE_REQUEST -> {
                val request = Share.Request()
                request.fromBundle(bundle)
                if (request.validate()) {
                    eventHandler.onRequest(request)
                    true
                } else {
                    false
                }
            }
            Constants.TIKTOK.SHARE_RESPONSE -> {
                val response = Share.Response()
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
