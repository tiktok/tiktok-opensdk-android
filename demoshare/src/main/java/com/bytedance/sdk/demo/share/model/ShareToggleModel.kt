package com.bytedance.sdk.demo.share.model


data class ShareToggleModel(
    val title: String,
    val desc: String,
    val isOn: Boolean = false,
    val onToggleChange: (Boolean) -> Unit
) : DataModel {
    override val viewType = ViewType.TOGGLE
}