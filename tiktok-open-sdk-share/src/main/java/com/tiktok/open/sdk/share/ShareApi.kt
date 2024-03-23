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
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.tiktok.open.sdk.core.appcheck.TikTokAppCheckUtil
import com.tiktok.open.sdk.share.constants.Constants
import com.tiktok.open.sdk.share.constants.Constants.SHARE_TIKTOK_INSTALL_LANDING_FAIL
import com.tiktok.open.sdk.share.constants.Keys
import com.tiktok.open.sdk.share.constants.LocaleMappings.TIKTOK_T_LOCALES
import com.tiktok.open.sdk.share.constants.OneLinkConstants.SCHEMA_HTTPS
import com.tiktok.open.sdk.share.constants.OneLinkConstants.TIKTOK_M_PLAYSTORE_ENDPOINT
import com.tiktok.open.sdk.share.constants.OneLinkConstants.TIKTOK_M_PLAYSTORE_HOST
import com.tiktok.open.sdk.share.constants.OneLinkConstants.TIKTOK_T_PLAYSTORE_ENDPOINT
import com.tiktok.open.sdk.share.constants.OneLinkConstants.TIKTOK_T_PLAYSTORE_HOST
import com.tiktok.open.sdk.share.constants.ShareErrorCodes.SUCCESS
import com.tiktok.open.sdk.share.model.LaunchResult
import com.tiktok.open.sdk.share.model.MediaContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Provides an interface for sharing media to TikTok.
 * @param activity your activity
 */
class ShareApi(private val activity: Activity) {

    fun share(request: ShareRequest): LaunchResult {
        TikTokAppCheckUtil.getInstalledTikTokApp(activity)?.let {
            share(request, it.appPackageName)
            return LaunchResult(
                result = SUCCESS,
            )
        }

        return try {
            openLandOption()
            LaunchResult(
                result = SUCCESS,
            )
        } catch (e: Exception) {
            LaunchResult(
                result = SHARE_TIKTOK_INSTALL_LANDING_FAIL,
            )
        }
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

    private fun getHostAndEndpoint(): Pair<String, String> {
        return if (TIKTOK_T_LOCALES.contains(Locale.getDefault().country)) {
            Pair(TIKTOK_T_PLAYSTORE_HOST, TIKTOK_T_PLAYSTORE_ENDPOINT)
        } else {
            Pair(TIKTOK_M_PLAYSTORE_HOST, TIKTOK_M_PLAYSTORE_ENDPOINT)
        }
    }

    private fun composeUrl(gaId: String, host: String, endpoint: String): Uri.Builder {

        return Uri.Builder()
            .scheme(SCHEMA_HTTPS)
            .authority(host)
            .path(endpoint)
            .appendQueryParameter("advertising_id", gaId)
    }

    private fun openLandOption() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val hostAndEndpoint = getHostAndEndpoint()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        GlobalScope.launch(Dispatchers.IO) {
            val gaId = if (resultCode == ConnectionResult.SUCCESS) {
                try {
                    AdvertisingIdClient.getAdvertisingIdInfo(activity).id.toString()
                } catch (e: Exception) {
                    ""
                }
            } else {
                ""
            }
            launch(Dispatchers.Main) {
                try {
                    CustomTabsIntent.Builder().build().launchUrl(
                        activity,
                        composeUrl(
                            gaId,
                            hostAndEndpoint.first,
                            hostAndEndpoint.second
                        ).build()
                    )
                } catch (e: Exception) {
                }
            }
        }
    }
}
