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
import android.os.Bundle
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.common.model.EntryComponent
import com.bytedance.sdk.open.tiktok.utils.AppUtils
import com.bytedance.sdk.open.tiktok.utils.AppUtils.getPlatformSDKVersion

class ShareService(
    private val context: Context,
    private val clientKey: String,
) {
    fun share(request: Share.Request, entryComponent: EntryComponent): Boolean {
        if (!request.validate()) {
            return false
        }
        val bundle = Bundle().apply {
            if (getPlatformSDKVersion(context, entryComponent.tiktokPackage, entryComponent.tiktokPlatformComponent)
                >= Keys.API.MIN_SDK_NEW_VERSION_API
            ) {
                putAll(request.toBundle(clientKey = clientKey))
            }
        }

        val intent = Intent().apply {
            component = ComponentName(
                entryComponent.tiktokPackage,
                AppUtils.componentClassName(BuildConfig.TIKTOK_COMPONENT_PATH, entryComponent.tiktokComponent)
            )
            putExtras(bundle)
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
