package com.bytedance.sdk.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bytedance.sdk.account.MainActivity.IS_ENVIRONMENT_BOE
import com.bytedance.sdk.account.MainActivity.SHARE_PREFS
import com.bytedance.sdk.account.user.IUserApiBack
import com.bytedance.sdk.account.user.NetworkManager
import com.bytedance.sdk.account.user.UploadSoundApiCallback
import com.bytedance.sdk.account.user.bean.UserInfo
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*


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

    val uploadSound by lazy<Button> {
        findViewById(R.id.uploadSound)
    }

    val uploadResult by lazy<TextView> {
        findViewById(R.id.uploadResult)
    }

    val backBtn by lazy<Button> {
        findViewById(R.id.back)
    }

    lateinit var accessToken: String;
    lateinit var openId: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_activity)

        mUserInfoLayout.visibility = View.GONE
        mLoadingGroup.visibility = View.VISIBLE

        if (!intent.getBooleanExtra(MainActivity.SHARE_SOUND, false)) {
            uploadSound.visibility = View.GONE
        }

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
                                override fun onResult(success: Boolean, errorMsg: String, info: UserInfo?, accessToken: String, openId: String) {
                                    mLoadingGroup.visibility = View.GONE
                                    this@UserInfoActivity.accessToken = accessToken
                                    this@UserInfoActivity.openId = openId
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

        uploadSound.setOnClickListener {
            val intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 1)
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            val myUri: Uri? = data?.data
            val cursor = contentResolver.query(myUri!!, null, null, null, null);

            myUri?.let {uri ->
                cursor?.let {
                    cursor.moveToFirst()
                    val idx: Int = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)
                    val path = cursor.getString(idx)
                    val extension = path.substring(path.lastIndexOf("."));
                    cursor.close()

                    try {
                        val input = contentResolver.openInputStream(uri)
                        input?.let { inputStream ->
                            val file = File(getExternalFilesDir(null), "/audioData")
                            file.mkdirs()
                            val output: OutputStream = FileOutputStream(getExternalFilesDir(null).toString() + "/audioData/my_sound" + extension)
                            val chunk = ByteArray(1024)
                            var res: Int = inputStream.read(chunk)
                            while (res != -1) {
                                output.write(chunk, 0, res)
                                res = inputStream.read(chunk)
                            }
                            output.flush()
                            output.close()
                            inputStream.close()
                        }

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val myFile = File(getExternalFilesDir(null).toString() + "/audioData/my_sound" + extension)

                    // create RequestBody instance from file
                    val requestFile: RequestBody = RequestBody.create(
                            MediaType.parse("audio/*"),
                            myFile
                    )

                    // MultipartBody.Part is used to send also the actual file name
                    val body = MultipartBody.Part.createFormData("sound_file", myFile.name, requestFile)
                    intent.getStringExtra(MainActivity.CODE_KEY)?.let {
                        val sharedPreferences = getSharedPreferences(SHARE_PREFS, android.content.Context.MODE_PRIVATE)
                        val isBOE = sharedPreferences.getBoolean(IS_ENVIRONMENT_BOE, false)
                        NetworkManager().uploadSound(accessToken,
                                openId,
                                isBOE,
                                body,
                                object : UploadSoundApiCallback {
                                    override fun onResult(success: Boolean, errorMsg: String, errorCode: Int?) {
                                        if (success) {
                                            uploadResult.text = String.format("Upload Successful, status code: %d", errorCode)
                                        } else {
                                            uploadResult.text = String.format("Upload Failed, status code: %d, error message: %s", errorCode, errorMsg)
                                        }
                                    }
                                })
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}