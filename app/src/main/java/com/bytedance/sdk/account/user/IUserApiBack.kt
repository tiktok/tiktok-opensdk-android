package com.bytedance.sdk.account.user

import com.bytedance.sdk.account.user.bean.UserInfo

/**
 * 主要功能：
 * author: ChangLei
 * since: 2019/4/8
 */
interface IUserApiBack {

    fun onResult(success: Boolean, errorMsg: String, info: UserInfo?, accessToken: String, openId: String)
}