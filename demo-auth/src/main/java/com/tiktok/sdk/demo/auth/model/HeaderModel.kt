package com.tiktok.sdk.demo.auth.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

data class HeaderModel(val headerTitle: String) : DataModel {
    override val viewType = ViewType.HEADER
}
