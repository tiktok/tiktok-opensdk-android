package com.bytedance.sdk.demo.auth.model

import com.bytedance.sdk.demo.auth.ViewType

class HeaderModel(val header: String): DataModel {
    override val viewType = ViewType.HEADER
}