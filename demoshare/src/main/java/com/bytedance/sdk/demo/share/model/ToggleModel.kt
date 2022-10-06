package com.bytedance.sdk.demo.share.model

data class ToggleModel(
    val title: String,
    val desc: String,
    val isOn: Boolean,
    val toggleType: ToggleType
) : DataModel {
    override val viewType = ViewType.TOGGLE
}

enum class ToggleType {
    ENABLE_CUSTOMIZE_KEY,
    DISABLE_MUSIC,
    GREEN_SCREEN,
    AUTO_ATTACH_ANCHOR,
}
