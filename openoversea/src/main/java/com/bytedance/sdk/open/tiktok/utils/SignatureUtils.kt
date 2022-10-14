package com.bytedance.sdk.open.tiktok.utils

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

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.TextUtils

internal object SignatureUtils {
    // validate tiktok or musically app's signature
    fun validateSign(context: Context?, pkgName: String, sign: String): Boolean {
        if (TextUtils.isEmpty(pkgName) || TextUtils.isEmpty(sign) || context == null) {
            return false
        }
        val signList = getMd5Signs(context, pkgName)
        return signList?.find {
            sign.equals(it, ignoreCase = true)
        } != null
    }

    fun getMd5Signs(context: Context, pkgName: String?): List<String?>? {
        if (pkgName.isNullOrEmpty()) {
            return null
        }
        val packageInfo: PackageInfo = try {
            context.packageManager.getPackageInfo(pkgName!!, PackageManager.GET_SIGNATURES)
        } catch (ex: PackageManager.NameNotFoundException) {
            return null
        }
        val signList: List<String?>? = packageInfo.signatures?.map {
            Md5Utils.hexDigest(it.toByteArray())
        }
        return signList
    }

    fun packageSignature(signs: List<String?>?): String? {
        if (signs != null && !signs.isEmpty()) {
            val sb = StringBuilder()
            for (sign in signs) {
                sb.append(",").append(sign)
            }
            return if (sb.isEmpty()) null else sb.substring(1)
        }
        return null
    }
}
