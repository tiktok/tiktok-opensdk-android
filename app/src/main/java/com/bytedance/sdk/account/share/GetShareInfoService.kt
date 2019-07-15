package com.bytedance.sdk.account.share

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author yangjie
 * @date 2019-07-15
 */
interface GetShareInfoService {

    @GET("/share/id")
    fun getShareId(@Query("access_token") access_token: String,
                   @Query("need_callback") need_callback: Boolean): Call<ShareIdResp>

    @GET("/oauth/client_token/ ")
    fun getAccessToken(@Query("client_key") clientKey: String, @Query("client_secret") clientSecret: String,
                       @Query("grant_type") grantType: String): Call<ShareAccessTokenResp>

}
