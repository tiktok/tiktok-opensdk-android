package com.bytedance.sdk.open.tiktok.utils

import android.view.View

object OpenUtils {
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