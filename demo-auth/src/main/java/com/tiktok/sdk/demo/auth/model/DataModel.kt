package com.tiktok.sdk.demo.auth.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

enum class ViewType(val value: Int) {
    SCOPE(0), LOGO(1), HEADER(2), CONFIG(3);
    companion object {
        fun typeFrom(value: Int): ViewType {
            return when (value) {
                1 -> { LOGO }
                2 -> { HEADER }
                3 -> { CONFIG }
                else -> { SCOPE }
            }
        }
    }
}

interface DataModel {
    val viewType: ViewType
}
