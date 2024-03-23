package com.tiktok.sdk.demo.auth.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import androidx.annotation.StringRes

data class ScopeModel(
    val type: ScopeType,
    @StringRes
    val descRes: Int,
    val isOn: Boolean,
    val isEditable: Boolean
) : DataModel {
    override val viewType = ViewType.SCOPE
}

enum class ScopeType(val value: String) {
    USER_INFO_BASIC("user.info.basic"),
    VIDEO_UPLOAD("video.upload"),
    VIDEO_LIST("video.list"),
    FEED_FORYOU_LIST("feed.foryou.list");

    companion object {
        fun fromValue(value: String): ScopeType? {
            return values().find { it.value == value }
        }
    }
}
