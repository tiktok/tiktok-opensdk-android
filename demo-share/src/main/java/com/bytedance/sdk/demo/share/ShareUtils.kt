package com.bytedance.sdk.demo.share

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

import com.google.gson.Gson
import kotlin.text.StringBuilder

class ShareUtils {
    companion object {
        fun parseHashtags(hashtagString: String): ArrayList<String> {
            val hashtags = ArrayList<String>()
            var sb: StringBuilder? = null
            fun collectHashtag() {
                if (!sb.isNullOrEmpty()) {
                    hashtags.add(sb.toString())
                }
            }
            for (i in hashtagString.indices) {
                when (hashtagString[i]) {
                    '#' -> {
                        collectHashtag()
                        sb = StringBuilder()
                    }
                    ' ' -> {
                        collectHashtag()
                        sb = null
                    }
                    else -> {
                        sb?.append(hashtagString[i])
                    }
                }
            }
            collectHashtag()
            return hashtags
        }

        fun parseAnchorSourceType(sourceTypeString: String): String? {
            val sourceTypes = sourceTypeString.split(" ")
            return sourceTypes.find {
                it.isNotEmpty()
            }
        }

        fun parseJSON(jsonString: String): Map<String, String>? {
            return try {
                var json = jsonString
                if (!jsonString.startsWith("{")) {
                    json = "{$json"
                }
                if (!json.endsWith("}")) {
                    json = "$json}"
                }
                val gson = Gson()
                (gson.fromJson(json, Map::class.java) as Map<String, String>)
            } catch (e: Exception) {
                null
            }
        }
    }
}
