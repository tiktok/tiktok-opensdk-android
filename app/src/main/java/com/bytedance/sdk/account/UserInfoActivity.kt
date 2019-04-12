package com.bytedance.sdk.account

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
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
                NetworkManager().getUserInfo(code, BuildConfig.CLIENT_KEY, BuildConfig.CLIENT_SECRET, object : IUserApiBack {
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
            }).start()
        }
        else {
            Toast.makeText(this, "授权失败", Toast.LENGTH_LONG).show()
        }
    }
}