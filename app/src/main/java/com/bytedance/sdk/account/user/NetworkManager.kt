package com.bytedance.sdk.account.user

import com.bytedance.sdk.account.MainActivity
import com.bytedance.sdk.account.user.bean.AccessTokenResponse
import com.bytedance.sdk.account.user.bean.UserInfoResponse
import com.bytedance.sdk.open.aweme.CommonConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

/**
 * 主要功能：
 * author: ChangLei
 * since: 2019/4/3
 */
class NetworkManager {

    private fun <T> createApi(apiClass: Class<T>): T {
        var retrofitBuilder = Retrofit.Builder()
        if (MainActivity.targetAppId == CommonConstants.TARGET_APP.TIKTOK) {
            retrofitBuilder.baseUrl("https:\\open-api.tiktok.com")
        }else {
            retrofitBuilder.baseUrl("https:\\open.douyin.com")
        }
        var retrofit = retrofitBuilder
                .build()
        return retrofit.create(apiClass)
    }

    fun getUserInfo(code: String, clientKey: String, clientSecret: String, listener: IUserApiBack) {
        val userInfoApi = createApi(GetUserInfoServie::class.java)
        userInfoApi.getAccessToken(clientKey, clientSecret, code, "authorization_code")
                .enqueue(object : Callback<AccessTokenResponse> {
                    override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                        listener.onResult(false, t.toString(), null)
                    }

                    override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.data?.let {
                                userInfoApi.getUserInfo(it.accessToken, it.openid)
                                        .enqueue(object : Callback<UserInfoResponse> {
                                            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                                                listener.onResult(false, t.toString(), null)
                                            }

                                            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                                                if (response.isSuccessful) {
                                                    listener.onResult(true, "获取用户信息成功", response.body()?.data)
                                                }
                                                else {
                                                    listener.onResult(false, response.message(), null)
                                                }
                                            }

                                        })
                            }
                        }
                        else {
                            listener.onResult(false, response.message(), null)
                        }
                    }

                })
    }

}