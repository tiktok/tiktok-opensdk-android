package com.bytedance.sdk.open.tiktok.common.model

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.utils.AppUtils

class Base {
    abstract class Request {
        internal abstract val type: Int

        internal abstract val resultActivityComponent: ResultActivityComponent

        internal abstract fun validate(): Boolean

        abstract fun toBundle(clientKey: String): Bundle

        internal open fun toBundle(): Bundle {
            return Bundle().apply {
                putInt(Keys.Base.TYPE, type)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_NAME, BuildConfig.SDK_OVERSEA_NAME)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_VERSION, BuildConfig.SDK_OVERSEA_VERSION)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, BuildConfig.SDK_OVERSEA_NAME)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, BuildConfig.SDK_OVERSEA_VERSION)
                putString(Keys.Base.CALLER_PKG, resultActivityComponent.packageName)
                putString(
                    Keys.Base.FROM_ENTRY,
                    AppUtils.componentClassName(
                        packageName = resultActivityComponent.packageName,
                        classPath = resultActivityComponent.className
                    )
                )
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
