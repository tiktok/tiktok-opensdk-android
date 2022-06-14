package com.bytedance.sdk.open.tiktok.utils

import java.security.MessageDigest

sealed class Md5Utils {
    companion object {
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

        fun hexDigest(bytes: ByteArray?): String? {
            var res: String? = null
            try {
                val messageDigest = MessageDigest.getInstance("MD5")
                messageDigest.update(bytes)
                val tmp: ByteArray = messageDigest.digest()
                val charStr = CharArray(32)
                var k = 0
                for (i in hexDigits.indices) {
                    val b = tmp[i].toInt() // TODO: chen.wu change to Int?
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
}