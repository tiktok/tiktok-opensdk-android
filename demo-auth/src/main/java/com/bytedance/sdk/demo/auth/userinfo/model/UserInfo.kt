package com.bytedance.sdk.demo.auth.userinfo.model

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("open_id")val openId: String,
    @SerializedName("union_id")val unionId: String,
    @SerializedName("display_name")val nickName: String,
    @SerializedName("avatar")val avatar: String,
    @SerializedName("error_code")val errorCode: Int,
    @SerializedName("description")val description: String
)
