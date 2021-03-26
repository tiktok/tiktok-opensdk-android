package com.bytedance.sdk.account

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bytedance.sdk.account.MainActivity.IS_ENVIRONMENT_BOE
import com.bytedance.sdk.account.MainActivity.SHARE_PREFS
import com.bytedance.sdk.account.user.IUserApiBack
import com.bytedance.sdk.account.user.NetworkManager
import com.bytedance.sdk.account.user.bean.UserInfo

/**
 * 主要功能：用于展示授权之后的用户信息
 * author: ChangLei
 * since: 2019/4/8
 */
class UserInfoActivity: Activity() {

    val mUserInfoLayout by lazy<View> {
        findViewById(R.id.user_info)
    }

    val mLoadingGroup by lazy<View> {
        findViewById(R.id.loading_group)
    }

    val mUserNameView by lazy<TextView> {
        findViewById(R.id.user_name)
    }

    val mUserAvarView by lazy<ImageView> {
        findViewById(R.id.icon)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_activity)

        mUserInfoLayout.visibility = View.GONE
        mLoadingGroup.visibility = View.VISIBLE

        if (intent.hasExtra(MainActivity.CODE_KEY)) {
            val code = intent.getStringExtra(MainActivity.CODE_KEY)
            Thread(Runnable {
                code?.let {
                    val sharedPreferences = getSharedPreferences (SHARE_PREFS, android.content.Context.MODE_PRIVATE)
                    val isBOE = sharedPreferences.getBoolean(IS_ENVIRONMENT_BOE, false)
                    NetworkManager().getUserInfo(
                            it,
                            if (isBOE && BuildConfig.CLIENT_KEY_BOE.isNotEmpty()) BuildConfig.CLIENT_KEY_BOE else BuildConfig.CLIENT_KEY,
                            isBOE,
                            this,
                            object : IUserApiBack {
                                override fun onResult(success: Boolean, errorMsg: String, info: UserInfo?) {
                                    mLoadingGroup.visibility = View.GONE
                                    if (success) {
                                        mUserInfoLayout.visibility = View.VISIBLE
                                        mUserNameView.text = info?.nickName ?: ""

                                        Glide.with(this@UserInfoActivity).load(info?.avatar).into(mUserAvarView)
                                    } else {
                                        Toast.makeText(this@UserInfoActivity, errorMsg, Toast.LENGTH_LONG).show()
                                    }
                                }

                            })
                }
            }).start()
        }
        else {
            Toast.makeText(this, "Authorization Failed", Toast.LENGTH_LONG).show()
        }
    }
}