package com.bytedance.sdk.account.user

import com.bytedance.sdk.account.NetUtils
import com.bytedance.sdk.account.user.bean.AccessTokenResponse
import com.bytedance.sdk.account.user.bean.UserInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 主要功能：
 * author: ChangLei
 * since: 2019/4/3
 */
class NetworkManager {


    fun getUserInfo(code: String, clientKey: String, clientSecret: String, isBoe: Boolean, listener: IUserApiBack) {
        val userInfoApi = NetUtils.createApi(GetUserInfoServie::class.java, isBoe)
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
                                                    listener.onResult(true, "Succeeded in obtaining user information", response.body()?.data)
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