package com.bytedance.sdk.demo.share.model

import androidx.lifecycle.MutableLiveData

class EditModel(val title: String, val desc: String, val text: MutableLiveData<String> = MutableLiveData("")): DataModel {
    override val viewType = ViewType.EDIT_TEXT

}