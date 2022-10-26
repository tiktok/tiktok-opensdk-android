package com.bytedance.sdk.open.tiktok.share

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.core.appcheck.AppCheckFactory
import com.bytedance.sdk.open.tiktok.core.constants.Constants.APIType
import com.bytedance.sdk.open.tiktok.core.constants.Constants.TIKTOK.SHARE_ACTIVITY_NAME
import com.bytedance.sdk.open.tiktok.core.constants.Constants.TIKTOK.TIKTOK_SHARE_COMPONENT_PATH
import com.bytedance.sdk.open.tiktok.core.utils.AppUtils
import com.bytedance.sdk.open.tiktok.share.constants.Constants
import com.bytedance.sdk.open.tiktok.share.constants.Keys

class ShareApi(
    private val context: Context,
    private val clientKey: String,
    private val apiEventHandler: ShareApiEventHandler,
) {

    companion object {
        fun isShareSupported(context: Context) = AppCheckFactory.getApiCheck(context, APIType.SHARE) != null
        fun isShareFileProviderSupported(context: Context) = (AppCheckFactory.getApiCheck(context, APIType.SHARE)?.isShareFileProviderSupported ?: false)
    }

    fun handleResultIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return false
        }
        val bundle = intent.extras ?: return false
        val type = bundle.getInt(Keys.Share.TYPE)
        if (type == Constants.SHARE_RESPONSE) {
            apiEventHandler.onResponse(bundle.toShareResponse())
            return true
        }
        return false
    }

    fun share(request: Share.Request): Boolean {
        apiEventHandler.onRequest(request)
        AppCheckFactory.getApiCheck(context, APIType.SHARE)?.let {
            return share(request, it.appPackageName)
        }
        apiEventHandler.onResponse(
            Share.Response(
                errorCode = Constants.ShareError.UNSUPPORTED,
                errorMsg = "TikTok is not installed or doesn't support the sharing feature",
                state = null,
                subErrorCode = null,
            )
        )
        return false
    }

    private fun share(request: Share.Request, packageName: String): Boolean {
        if (!request.validate()) {
            return false
        }
        val intent = Intent().apply {
            component = ComponentName(
                packageName,
                AppUtils.componentClassName(TIKTOK_SHARE_COMPONENT_PATH, SHARE_ACTIVITY_NAME)
            )
            putExtras(
                request.toBundle(
                    clientKey = clientKey,
                    sdkName = BuildConfig.SHARE_SDK_NAME,
                    sdkVersion = BuildConfig.SHARE_SDK_VERSION
                )
            )
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
