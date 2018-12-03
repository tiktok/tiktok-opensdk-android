package com.tiktok.open.sdk.core.utils

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import java.security.MessageDigest

object SignatureUtils {
    fun validateSign(context: Context, pkgName: String, sign: String): Boolean {
        return getSignatureList(context, pkgName).toSet().contains(sign)
    }

    private fun getSignatureList(context: Context, pkgName: String): List<String> {
        val packageManager = context.packageManager
        val signatureList: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val sig = packageManager.getPackageInfo(pkgName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
            if (sig.hasMultipleSigners()) {
                sig.apkContentsSigners.map {
                    it.toCharsString()
                }
            } else {
                sig.signingCertificateHistory.map {
                    it.toCharsString()
                }
            }
        } else {
            val sig = packageManager.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES).signatures
            sig.map {
                it.toCharsString()
            }
        }
        return signatureList
    }

    fun getCallerSHA256Certificates(context: Context, pkgName: String): String {
        val packageManager = context.packageManager
        val signatureList: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val sig = packageManager.getPackageInfo(pkgName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
            if (sig.hasMultipleSigners()) {
                sig.apkContentsSigners.map {
                    it.hashBy256()
                }
            } else {
                sig.signingCertificateHistory.map {
                    it.hashBy256()
                }
            }
        } else {
            val sig = packageManager.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES).signatures
            sig.map {
                it.hashBy256()
            }
        }
        return signatureList.joinToString(",")
    }

    private fun Signature.hashBy256(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(toByteArray())
        return digest.digest().bytesToHex()
    }

    private fun ByteArray.bytesToHex(): String {
        val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        val hexChars = CharArray(size * 2)
        var v: Int
        for (j in indices) {
            v = this[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}
