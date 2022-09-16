package com.bytedance.sdk.demo.share.model

import androidx.lifecycle.MutableLiveData

class InfoModel(val title: String, val desc: String, val info: MutableLiveData<String> = MutableLiveData("")) : DataModel {
    override val viewType = ViewType.INFO
}
