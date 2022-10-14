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

import android.R
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout

object ViewUtils {

    fun setStatusBarColor(activity: Activity, color: Int) { // TODO: chen.wu refactor to avoid base 1.0.5
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // use standard mode
            activity.window.statusBarColor = color
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // generate a view to simulation status bar
            val statusView = createStatusBarView(activity, color)
            // add status view
            val decorView = activity.window.decorView as ViewGroup
            decorView.addView(statusView)
            val content = activity.findViewById<View>(R.id.content) as ViewGroup
            val rootView = content.getChildAt(content.childCount - 1) as ViewGroup
            rootView.fitsSystemWindows = true
            rootView.clipToPadding = true
        }
    }

    private fun createStatusBarView(activity: Activity, color: Int): View {
        val statusBarView = View(activity)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            getStatusBarHeight(activity)
        )
        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(color)
        return statusBarView
    }

    private fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }
}
