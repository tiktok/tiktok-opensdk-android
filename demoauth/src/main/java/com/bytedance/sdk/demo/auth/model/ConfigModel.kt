package com.bytedance.sdk.demo.auth.model

import androidx.lifecycle.MutableLiveData

class ConfigModel(val title: String, val desc: String, var isOn: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)): DataModel {
    override val viewType = ViewType.CONFIG
}