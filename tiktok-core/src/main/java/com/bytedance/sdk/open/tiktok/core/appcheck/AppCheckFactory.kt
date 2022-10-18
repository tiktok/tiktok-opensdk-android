package com.bytedance.sdk.open.tiktok.core.appcheck

import android.content.Context
import com.bytedance.sdk.open.tiktok.core.constants.Constants

object AppCheckFactory {

    fun getApiCheck(context: Context, apiType: Constants.APIType): IAppCheck? {
        when (apiType) {
            Constants.APIType.AUTH -> for (appCheck in arrayOf(MusicallyCheck(context), TikTokCheck(context))) {
                if (appCheck.isAuthSupported) {
                    return appCheck
                }
            }
            Constants.APIType.SHARE -> for (appCheck in arrayOf(MusicallyCheck(context), TikTokCheck(context))) {
                if (appCheck.isShareSupported) {
                    return appCheck
                }
            }
        }
        return null
    }
}
