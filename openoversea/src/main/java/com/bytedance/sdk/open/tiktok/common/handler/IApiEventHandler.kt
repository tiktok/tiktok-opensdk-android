package com.bytedance.sdk.open.tiktok.common.handler

import android.content.Intent
import com.bytedance.sdk.open.tiktok.common.model.Base

interface IApiEventHandler {
    fun onRequest(req: Base.Request) = Unit

    fun onResponse(resp: Base.Response) = Unit

    fun onErrorIntent(intent: Intent?) = Unit
}
