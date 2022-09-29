package com.bytedance.sdk.open.tiktok.common.model

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.utils.AppUtils

class Base {
    abstract class Request {
        internal abstract val type: Int

        internal open val localEntry: String? = null

        internal abstract fun validate(): Boolean

        open fun toBundle(): Bundle = Bundle()

        abstract fun toBundle(clientKey: String, callerPackageName: String, callerVersion: String? = null): Bundle

        internal open fun toBundle(callerPackageName: String, callerVersion: String?): Bundle {
            return Bundle().apply {
                putInt(Keys.Base.TYPE, type)
                putString(Keys.Base.CALLER_PKG, callerPackageName)
                putString(Keys.Base.CALLER_BASE_OPEN_VERSION, callerVersion)
                putString(
                    Keys.Base.FROM_ENTRY,
                    AppUtils.componentClassName(
                        packageName = callerPackageName,
                        classPath = localEntry ?: BuildConfig.DEFAULT_ENTRY_ACTIVITY
                    )
                )
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_NAME, BuildConfig.SDK_OVERSEA_NAME)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_VERSION, BuildConfig.SDK_OVERSEA_VERSION)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, BuildConfig.SDK_OVERSEA_NAME)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, BuildConfig.SDK_OVERSEA_VERSION)
            }
        }
    }

    abstract class Response {
        abstract val type: Int
        abstract val errorCode: Int
        abstract val errorMsg: String?
        abstract val extras: Bundle?

        val isSuccess: Boolean
            get() = errorCode == Constants.BaseError.OK

        open fun toBundle(): Bundle =
            Bundle().apply {
                putInt(Keys.Base.TYPE, type)
                putInt(Keys.Base.ERROR_CODE, errorCode)
                errorMsg?.let { this.putString(Keys.Base.ERROR_MSG, it) }
                putBundle(Keys.Base.EXTRA, extras)
            }
    }
}
