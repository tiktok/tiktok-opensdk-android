package com.bytedance.sdk.account.user

import com.bytedance.sdk.account.user.bean.UploadSoundResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UploadSoundService {

    @Multipart
    @POST("/share/sound/upload")
    fun uploadSound(@Query("access_token")accessToken: String,
                    @Query("open_id")openId: String,
                    @Part file: MultipartBody.Part): Call<UploadSoundResponse>
}