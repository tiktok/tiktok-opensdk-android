package com.bytedance.sdk.demo.share.model

data class EditModel(
    val title: String,
    val desc: String,
    var text: String = "",
    val enabled: Boolean = true
) : DataModel {
    override val viewType = ViewType.EDIT_TEXT
}
