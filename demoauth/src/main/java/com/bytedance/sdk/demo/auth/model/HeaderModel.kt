package com.bytedance.sdk.demo.auth.model

data class HeaderModel(val headerTitle: String) : DataModel {
    override val viewType = ViewType.HEADER
}
