package com.bytedance.sdk.demo.share.model

import androidx.lifecycle.MutableLiveData

class EditModel(val title: String,
                val desc: String,
                val text: MutableLiveData<String> = MutableLiveData(""),
                val enabled: MutableLiveData<Boolean> = MutableLiveData(true)): DataModel {
    override val viewType = ViewType.EDIT_TEXT
}