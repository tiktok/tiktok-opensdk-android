package com.bytedance.sdk.demo.share.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
