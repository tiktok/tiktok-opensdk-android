package com.bytedance.sdk.demo.auth.model

import androidx.lifecycle.MutableLiveData

class ConfigModel(
    val title: String,
    val desc: String,
    val isOn: Boolean,
    val toggleListener: (Boolean) -> Unit,
) : DataModel {
    override val viewType = ViewType.CONFIG
}
