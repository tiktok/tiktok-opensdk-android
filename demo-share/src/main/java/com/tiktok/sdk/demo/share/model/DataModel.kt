package com.tiktok.sdk.demo.share.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

enum class ViewType(val value: Int) {
    TOGGLE(0), LOGO(1), HEADER(2), EDIT_TEXT(3), INFO(4);
    companion object {
        fun typeFrom(value: Int): ViewType {
            return when (value) {
                1 -> { LOGO }
                2 -> { HEADER }
                3 -> { EDIT_TEXT }
                4 -> { INFO }
                else -> { TOGGLE }
            }
        }
    }
}

interface DataModel {
    val viewType: ViewType
}
