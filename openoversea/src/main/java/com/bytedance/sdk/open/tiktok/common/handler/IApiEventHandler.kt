package com.bytedance.sdk.open.tiktok.common.handler

import android.content.Intent
import com.bytedance.sdk.open.tiktok.common.model.Base

interface IApiEventHandler {
    fun onReq(req: Base.Request?)

    fun onResp(resp: Base.Response?)

    fun onErrorIntent(intent: Intent?)
}
