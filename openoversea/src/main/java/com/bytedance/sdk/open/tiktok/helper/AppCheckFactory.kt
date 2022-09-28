package com.bytedance.sdk.open.tiktok.helper

import android.content.Context
import com.bytedance.sdk.open.tiktok.base.IAppCheck
import com.bytedance.sdk.open.tiktok.common.constants.Constants

internal object AppCheckFactory {

    internal fun getApiCheck(context: Context, apiType: Constants.APIType): IAppCheck? {
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
