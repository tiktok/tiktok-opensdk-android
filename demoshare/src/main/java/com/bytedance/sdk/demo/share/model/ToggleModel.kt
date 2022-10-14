package com.bytedance.sdk.demo.share.model

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

data class ToggleModel(
    val title: String,
    val desc: String,
    val isOn: Boolean,
    val toggleType: ToggleType
) : DataModel {
    override val viewType = ViewType.TOGGLE
}

enum class ToggleType {
    ENABLE_CUSTOMIZE_KEY,
    DISABLE_MUSIC,
    GREEN_SCREEN,
    AUTO_ATTACH_ANCHOR,
}
