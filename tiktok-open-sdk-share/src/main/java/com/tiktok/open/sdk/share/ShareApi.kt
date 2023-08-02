package com.tiktok.open.sdk.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import com.tiktok.open.sdk.core.appcheck.TikTokAppCheckUtil
import com.tiktok.open.sdk.share.constants.Constants
import com.tiktok.open.sdk.share.constants.Keys
import com.tiktok.open.sdk.share.model.MediaContent

/**
 * Provides an interface for sharing media to TikTok.
 * @param activity your activity
 */
class ShareApi(private val activity: Activity) {

    fun share(request: ShareRequest): Boolean {
        TikTokAppCheckUtil.getInstalledTikTokApp(activity)?.let {
            return share(request, it.appPackageName)
        }
        return false
    }

    private fun share(request: ShareRequest, packageName: String): Boolean {
        if (!request.validate()) {
            return false
        }
        val intent = Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            component = ComponentName(
                packageName,
                "com.ss.android.ugc.aweme.share.SystemShareActivity"
            )
            putExtras(request.toBundle())
            type = getShareContentType(request.mediaContent)
            action = getShareContentAction(request.mediaContent)
        }
        return try {
            activity.startActivityForResult(intent, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getShareContentType(mediaContent: MediaContent): String {
        return if (mediaContent.mediaType == MediaType.IMAGE) {
            "image/*"
        } else {
            "video/*"
        }
    }

    private fun getShareContentAction(mediaContent: MediaContent): String {
        return if (mediaContent.mediaPaths.size > 1) {
            Intent.ACTION_SEND_MULTIPLE
        } else {
            Intent.ACTION_SEND
        }
    }

    fun getShareResponseFromIntent(intent: Intent?): ShareResponse? {
        if (intent == null) {
            return null
        }
        val bundle = intent.extras
        if (bundle?.getInt(Keys.Share.TYPE) == Constants.SHARE_RESPONSE) {
            return bundle.toShareResponse()
        }
        return null
    }
}
