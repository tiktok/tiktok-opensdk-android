package com.bytedance.sdk.demo.share.model

data class ToggleModel(
    val title: String,
    val desc: String,
    val isOn: Boolean = false,
    val onEditTextChange: (Boolean) -> Unit
) : DataModel {
    override val viewType = ViewType.TOGGLE
}
