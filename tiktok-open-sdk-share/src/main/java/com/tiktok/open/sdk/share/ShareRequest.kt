package com.tiktok.open.sdk.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.os.Bundle
import com.tiktok.open.sdk.core.model.Base
import com.tiktok.open.sdk.share.constants.Constants
import com.tiktok.open.sdk.share.constants.Keys
import com.tiktok.open.sdk.share.model.MediaContent
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/*
   * ShareRequest
   *
   * @param clientKey your app's client key.
   * @param mediaContent the media content to share.
   * @param shareFormat the sharing format.
   * @param packageName the package name of your app.
   * @param resultActivityFullPath the path of the activity being used to receive the share result information
 */
@Parcelize
data class ShareRequest(
    val clientKey: String,
    val mediaContent: MediaContent,
    val shareFormat: Format = Format.DEFAULT,
    val packageName: String,
    val resultActivityFullPath: String,
) : Base.Request() {

    @IgnoredOnParcel
    override var type: Int = Constants.SHARE_REQUEST

    override fun validate(): Boolean {
        if (shareFormat == Format.GREEN_SCREEN) {
            return mediaContent.mediaPaths.size == 1
        }
        return mediaContent.validate()
    }

    override fun toBundle(): Bundle {
        return super.toBundle(BuildConfig.SHARE_SDK_NAME, BuildConfig.SHARE_SDK_VERSION).apply {
            putString(Keys.Share.CLIENT_KEY, clientKey)
            putAll(mediaContent.toBundle())
            putInt(Keys.Share.SHARE_FORMAT, shareFormat.format)
            putString(Keys.Share.CALLER_PKG, packageName)
            putString(
                Keys.Share.CALLER_LOCAL_ENTRY,
                resultActivityFullPath
            )
        }
    }
}

/*
 * DEFAULT: Sharing video or image from your app to TikTok
 * GREEN_SCREEN: Sharing video or image as green-screen effect video. Green-screen video use the sharing video/image as the background and the creator's figure in the front.
 */
enum class Format(val format: Int) {
    DEFAULT(0),
    GREEN_SCREEN(1);
}

enum class MediaType {
    VIDEO,
    IMAGE;
}
