package com.bytedance.sdk.open.tiktok.auth.utils

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.view.View

internal object OpenUtils {
    fun setViewVisibility(v: View?, visible: Int) {
        if (v == null || v.visibility == visible || !visibilityValid(visible)) {
            return
        }
        v.visibility = visible
    }

    private fun visibilityValid(visible: Int): Boolean {
        return visible == View.VISIBLE || visible == View.GONE || visible == View.INVISIBLE
    }
}
