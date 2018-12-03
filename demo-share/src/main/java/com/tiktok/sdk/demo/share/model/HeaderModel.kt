package com.tiktok.sdk.demo.share.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

class HeaderModel(val title: String, val desc: String? = null) : DataModel {
    override val viewType = ViewType.HEADER
}
