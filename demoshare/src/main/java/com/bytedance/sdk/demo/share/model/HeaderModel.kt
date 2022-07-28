package com.bytedance.sdk.demo.share.model

class HeaderModel(val title: String, val desc: String? = null): DataModel {
    override val viewType = ViewType.HEADER

}