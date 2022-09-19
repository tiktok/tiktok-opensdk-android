package com.bytedance.sdk.demo.auth.model

class ConfigModel(
    val title: String,
    val desc: String,
    val isOn: Boolean,
    val toggleListener: (Boolean) -> Unit,
) : DataModel {
    override val viewType = ViewType.CONFIG
}
