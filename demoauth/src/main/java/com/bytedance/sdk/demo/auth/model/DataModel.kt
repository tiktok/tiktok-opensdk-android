package com.bytedance.sdk.demo.auth.model

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

enum class ViewType(val value: Int) {
    SCOPE(0), LOGO(1), HEADER(2), CONFIG(3);
    companion object {
        fun typeFrom(value: Int): ViewType {
            return when (value) {
                1 -> { LOGO }
                2 -> { HEADER }
                3 -> { CONFIG }
                else -> { SCOPE }
            }
        }
    }
}

interface DataModel {
    val viewType: ViewType
}
