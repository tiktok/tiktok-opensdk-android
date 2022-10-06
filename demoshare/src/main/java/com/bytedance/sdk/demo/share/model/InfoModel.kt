package com.bytedance.sdk.demo.share.model

data class InfoModel(val title: String, val desc: String, val info: String) : DataModel {
    override val viewType = ViewType.INFO
}
