package com.bytedance.sdk.demo.share.model

import androidx.lifecycle.MutableLiveData

class ToggleModel(val title: String, val desc: String, val isOn: MutableLiveData<Boolean> = MutableLiveData(false)) : DataModel {
    override val viewType = ViewType.TOGGLE
}
