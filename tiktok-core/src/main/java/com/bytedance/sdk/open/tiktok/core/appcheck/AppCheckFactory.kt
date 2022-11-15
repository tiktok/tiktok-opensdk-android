package com.bytedance.sdk.open.tiktok.core.appcheck

import android.content.Context
import com.bytedance.sdk.open.tiktok.core.constants.Constants

object AppCheckFactory {

    fun getApiCheck(context: Context, apiType: Constants.APIType): IAppCheck? {
        when (apiType) {
            Constants.APIType.AUTH -> for (appCheck in getAppCheckArray(context)) {
                if (appCheck.isAuthSupported) {
                    return appCheck
                }
            }
            Constants.APIType.SHARE -> for (appCheck in getAppCheckArray(context)) {
                if (appCheck.isShareSupported) {
                    return appCheck
                }
            }
        }
        return null
    }

    private fun getAppCheckArray(context: Context) = arrayListOf<IAppCheck>(
        object : AppCheckBase(context) {
            override val signature: String = "194326e82c84a639a52e5c023116f12a"
            override val appPackageName: String = "com.zhiliaoapp.musically"
        },
        object : AppCheckBase(context) {
            override val signature: String = "aea615ab910015038f73c47e45d21466"
            override val appPackageName: String = "com.ss.android.ugc.trill"
        },
    )
}
