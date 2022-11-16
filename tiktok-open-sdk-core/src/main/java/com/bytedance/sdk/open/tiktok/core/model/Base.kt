package com.bytedance.sdk.open.tiktok.core.model

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

import android.os.Bundle
import android.os.Parcelable
import com.bytedance.sdk.open.tiktok.core.constants.Constants
import com.bytedance.sdk.open.tiktok.core.constants.Keys

class Base {
    abstract class Request : Parcelable {
        abstract val type: Int

        abstract val packageName: String

        abstract val resultActivityFullPath: String

        abstract fun validate(): Boolean

        abstract fun toBundle(clientKey: String, sdkName: String, sdkVersion: String): Bundle

        fun toBundle(sdkName: String, sdkVersion: String): Bundle {
            return Bundle().apply {
                putInt(Keys.Base.TYPE, type)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_NAME, sdkName)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_COMMON_VERSION, sdkVersion)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_NAME, sdkName)
                putString(Keys.Base.CALLER_BASE_OPEN_SDK_VERSION, sdkVersion)
                putString(Keys.Base.CALLER_PKG, packageName)
                putString(Keys.Base.FROM_ENTRY, resultActivityFullPath)
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
