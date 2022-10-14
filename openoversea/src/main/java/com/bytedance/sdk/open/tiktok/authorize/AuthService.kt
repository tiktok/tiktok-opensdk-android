package com.bytedance.sdk.open.tiktok.authorize

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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.bytedance.sdk.open.tiktok.utils.AppUtils.componentClassName

class AuthService(
    private val context: Context,
    private val clientKey: String,
) {
    fun authorizeNative(authRequest: Auth.Request, authorizeAppPackageName: String, authorizeClassPath: String): Boolean {
        if (authorizeAppPackageName.isEmpty() || !authRequest.validate()) {
            return false
        }
        val bundle = authRequest.toBundle(
            clientKey = clientKey,
        )
        val intent = Intent().apply {
            component = ComponentName(authorizeAppPackageName, componentClassName(authorizeAppPackageName, authorizeClassPath))
            putExtras(bundle)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun authorizeWeb(req: Auth.Request): Boolean {
        return if (!req.validate()) {
            false
        } else {
            val bundle = req.toBundle(
                clientKey = clientKey,
            )
            val intent = Intent(context, WebAuthActivity::class.java).apply {
                putExtras(bundle)
            }
            try {
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
