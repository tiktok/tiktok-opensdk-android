package com.tiktok.open.sdk.share.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.os.Bundle
import android.os.Parcelable
import com.tiktok.open.sdk.share.MediaType
import com.tiktok.open.sdk.share.constants.Keys
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaContent(val mediaType: MediaType, val mediaPaths: ArrayList<String>) :
    Parcelable {
    fun toBundle(): Bundle {
        val pathKey = when (mediaType) {
            MediaType.IMAGE -> Keys.IMAGE_PATH
            MediaType.VIDEO -> Keys.VIDEO_PATH
        }
        return Bundle().apply {
            putStringArrayList(pathKey, mediaPaths)
        }
    }

    fun validate(): Boolean {
        return mediaPaths.isNotEmpty()
    }
}
