package com.bytedance.sdk.open.tiktok.utils

import android.view.View

sealed class OpenUtils {
    companion object {
        fun setViewVisibility(v: View?, visiable: Int) {
            if (v == null || v.visibility == visiable || !visibilityValid(visiable)) {
                return
            }
            v.visibility = visiable
        }

        private fun visibilityValid(visiable: Int): Boolean {
            return visiable == View.VISIBLE || visiable == View.GONE || visiable == View.INVISIBLE
        }
    }
}