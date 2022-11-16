package com.bytedance.sdk.open.tiktok.auth.utils

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
