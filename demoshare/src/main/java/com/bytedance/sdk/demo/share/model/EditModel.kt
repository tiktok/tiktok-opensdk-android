package com.bytedance.sdk.demo.share.model

import androidx.annotation.StringRes

data class EditModel(
    val type: TextType,
    @StringRes
    val titleRes: Int,
    @StringRes
    val descRes: Int,
    val text: String = "",
    val enabled: Boolean = true
) : DataModel {
    override val viewType = ViewType.EDIT_TEXT
}

enum class TextType(val value: String) {
    HASHTAG("Hashtag"),
    ANCHOR("Anchor"),
    EXTRA("Extra"),
}