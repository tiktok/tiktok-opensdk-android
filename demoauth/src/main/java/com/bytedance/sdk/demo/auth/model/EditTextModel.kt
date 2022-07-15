package com.bytedance.sdk.demo.auth.model

import com.bytedance.sdk.demo.auth.ViewType

data class EditTextModel(val title: String, val desc: String, val editText: String? = null): DataModel {
    override val viewType = ViewType.EDIT_TEXT
}