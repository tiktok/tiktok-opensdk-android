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

import java.security.MessageDigest

internal object Md5Utils {
    private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    fun hexDigest(string: String): String? {
        var res: String? = null
        try {
            res = hexDigest(string.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace() // TODO: chen.wu remove?
        }
        return res
    }

    fun hexDigest(bytes: ByteArray): String? {
        var res: String? = null
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(bytes)
            val tmp: ByteArray = messageDigest.digest()
            val charStr = CharArray(32)
            var k = 0
            for (i in hexDigits.indices) {
                val b = tmp[i].toInt()
                charStr[k++] = hexDigits[(b ushr 4) and 15]
                charStr[k++] = hexDigits[(b and 15)]
            }
            res = String(charStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return res
    }
}
