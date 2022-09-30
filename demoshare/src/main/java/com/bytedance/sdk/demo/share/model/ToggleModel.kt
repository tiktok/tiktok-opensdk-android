package com.bytedance.sdk.demo.share.model

class ToggleModel(val title: String, val desc: String, val isOn: Boolean = false) : DataModel {
    override val viewType = ViewType.TOGGLE
}
