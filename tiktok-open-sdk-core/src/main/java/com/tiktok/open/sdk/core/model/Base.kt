package com.tiktok.open.sdk.core.model

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.os.Bundle
import android.os.Parcelable
import com.tiktok.open.sdk.core.constants.Constants
import com.tiktok.open.sdk.core.constants.Keys

class Base {
    abstract class Request : Parcelable {
        abstract val type: Int

        abstract fun validate(): Boolean

        abstract fun toBundle(): Bundle

        fun toBundle(sdkName: String, sdkVersion: String): Bundle {
            return Bundle().apply {
                putInt(Keys.Base.TYPE, type)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, sdkName)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, sdkVersion)
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
