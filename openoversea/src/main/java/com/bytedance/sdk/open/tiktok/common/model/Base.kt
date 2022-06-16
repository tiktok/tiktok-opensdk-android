package com.bytedance.sdk.open.tiktok.common.model

import android.os.Bundle
import com.bytedance.sdk.open.tiktok.BuildConfig
import com.bytedance.sdk.open.tiktok.common.constants.Constants
import com.bytedance.sdk.open.tiktok.common.constants.Keys

class Base {
    abstract class Request() {
        abstract val type: Int
        var extras: Bundle? = null
        var callerPackage: String? = null
        var callerVersion: String? = null
        var callerLocalEntry: String? = null

        abstract fun validate(): Boolean

        open fun toBundle(): Bundle {
            return Bundle().apply {
                putInt(Keys.Base.TYPE, type)
                putBundle(Keys.Base.EXTRA, extras)
                putString(Keys.Base.CALLER_PKG, callerPackage)
                putString(Keys.Base.FROM_ENTRY, callerLocalEntry)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_NAME, BuildConfig.SDK_OVERSEA_NAME)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_VERSION, BuildConfig.SDK_OVERSEA_VERSION)
            }
        }
        open fun fromBundle(bundle: Bundle) {
            this.callerLocalEntry = bundle.getString(Keys.Base.FROM_ENTRY)
            this.callerPackage = bundle.getString(Keys.Base.CALLER_PKG)
            this.callerVersion = bundle.getString(Keys.Base.CALLER_BASE_OPEN_VERSION)
            this.extras = bundle.getBundle(Keys.Base.EXTRA)
        }
    }

    abstract class Response() {
        var errorCode: Int = 0
        var errorMsg: String? = null
        var extras: Bundle? = null
        abstract val type: Int
        val isCancelled: Boolean
            get() = if (errorCode != null) { errorCode!! == Constants.BaseError.ERROR_CANCEL }  else false
        val isSuccess: Boolean
            get() = if (errorCode == null) false else errorCode!! == Constants.BaseError.OK
        abstract fun validate(): Boolean

        open fun toBundle(): Bundle {
            return Bundle().apply {
                putInt(Keys.Base.ERROR_CODE, errorCode)
                errorMsg?.let { this.putString(Keys.Base.ERROR_MSG, it) }
                putInt(Keys.Base.TYPE, type)
                putBundle(Keys.Base.EXTRA, extras)
            }
        }
        open fun fromBundle(bundle: Bundle) {
            this.errorCode = bundle.getInt(Keys.Base.ERROR_CODE)
            this.errorMsg = bundle.getString(Keys.Base.ERROR_MSG)
            this.extras = bundle.getBundle(Keys.Base.EXTRA)
        }
    }
}