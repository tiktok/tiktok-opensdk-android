package com.bytedance.sdk.demo.share.model

import androidx.annotation.StringRes

data class EditTextModel(
    val type: EditTextType,
    @StringRes
    val titleRes: Int,
    @StringRes
    val descRes: Int,
    val text: String = "",
    val hint: String = "",
    val enabled: Boolean = true
) : DataModel {
    override val viewType = ViewType.EDIT_TEXT
}

enum class EditTextType {
    CLIENT_KEY,
    HASHTAG,
    ANCHOR,
    EXTRA,
}
