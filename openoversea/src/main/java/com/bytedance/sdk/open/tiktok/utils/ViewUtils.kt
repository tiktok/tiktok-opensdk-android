package com.bytedance.sdk.open.tiktok.utils

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
