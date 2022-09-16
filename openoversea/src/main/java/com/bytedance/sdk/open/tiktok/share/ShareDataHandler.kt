package com.bytedance.sdk.open.tiktok.share

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.handler.IApiEventHandler
import com.bytedance.sdk.open.tiktok.common.handler.IDataHandler

class ShareDataHandler : IDataHandler {
    override fun handle(type: Int, bundle: Bundle?, eventHandler: IApiEventHandler?): Boolean {
        if (bundle == null || eventHandler == null) {
            return false
        }
        if (type == Constants.TIKTOK.SHARE_REQUEST) {
            val request = Share.Request()
            request.fromBundle(bundle)
            return if (request.validate()) {
                eventHandler.onReq(request)
                true
            } else {
                false
            }
        } else if (type == Constants.TIKTOK.SHARE_RESPONSE) {
            val response = Share.Response()
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
