package com.bytedance.sdk.account.user

interface UploadSoundApiCallback {
    fun onResult(success: Boolean, errorMsg: String, errorCode: Int?)
}
