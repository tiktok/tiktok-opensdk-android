package com.bytedance.sdk.demo.auth.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

class ConfigModel(
    val title: String,
    val desc: String,
    val isOn: Boolean,
    val toggleListener: (Boolean) -> Unit,
) : DataModel {
    override val viewType = ViewType.CONFIG
}
