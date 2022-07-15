package com.bytedance.sdk.demo.auth.model

import com.bytedance.sdk.demo.auth.ViewType

data class ScopeModel(val title: String, val desc: String, val isOn: Boolean = false): DataModel {
    override val viewType = ViewType.SCOPE
}