package com.tiktok.open.sdk.core.utils

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

object AppUtils {
    fun concatPackageAndClassPath(packageName: String, classPath: String): String {
        return "$packageName.$classPath"
    }
}
