package com.bytedance.sdk.demo.auth.model

import androidx.lifecycle.MutableLiveData

data class ScopeModel(val title: String, val desc: String,
                      var isOn: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false),
                      var isEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)): DataModel {
    override val viewType = ViewType.SCOPE
}