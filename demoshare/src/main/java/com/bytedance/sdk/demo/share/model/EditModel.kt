package com.bytedance.sdk.demo.share.model

data class EditModel(
    val title: String,
    val desc: String,
    val text: String = "",
    val enabled: Boolean = true,
    val onEditTextChange: (String) -> Unit
) : DataModel {
    override val viewType = ViewType.EDIT_TEXT
}
