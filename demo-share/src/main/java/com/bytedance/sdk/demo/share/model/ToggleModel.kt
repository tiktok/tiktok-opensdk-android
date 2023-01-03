package com.bytedance.sdk.demo.share.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

data class ToggleModel(
    val title: String,
    val desc: String,
    val isOn: Boolean,
    val toggleType: ToggleType
) : DataModel {
    override val viewType = ViewType.TOGGLE
}

enum class ToggleType {
    ENABLE_CUSTOMIZE_KEY,
    GREEN_SCREEN,
}
