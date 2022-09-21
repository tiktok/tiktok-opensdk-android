package com.bytedance.sdk.open.tiktok.common.handler

import android.os.Bundle

interface IDataHandler {
    fun handle(type: Int, bundle: Bundle, eventHandler: IApiEventHandler): Boolean
}
