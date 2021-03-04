package com.bytedance.sdk.account.user.bean

import com.google.gson.annotations.SerializedName

class UploadSoundResponse (@SerializedName("description")var description: String,
                           @SerializedName("error_code")var errorCode: Int)
