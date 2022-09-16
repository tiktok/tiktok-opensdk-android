package com.bytedance.sdk.open.tiktok.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.TextUtils

object SignatureUtils {
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
