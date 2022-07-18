package com.bytedance.sdk.demo.auth.model

import com.bytedance.sdk.demo.auth.ViewType

data class ScopeModel(val title: String, val desc: String, var isOn: Boolean = false): DataModel {
    override val viewType = ViewType.SCOPE
}