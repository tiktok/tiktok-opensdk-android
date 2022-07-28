package com.bytedance.sdk.demo.share.model

import androidx.lifecycle.MutableLiveData

class HintedTextModel(val title: String, val desc: String,
                      val placeholder: String = "",
                      val text: MutableLiveData<String> = MutableLiveData(""),
                      val isEditable: MutableLiveData<Boolean> = MutableLiveData(false)): DataModel {
    override val viewType = ViewType.HINTED_TEXT
}