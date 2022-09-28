package com.bytedance.sdk.demo.share

import com.google.gson.Gson
import kotlin.text.StringBuilder

class ShareUtils {
    companion object {
        fun parseHashtags(hashtagString: String): List<String> {
            val hashtags = mutableListOf<String>()
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

        fun parseJSON(jsonString: String): Map<String, String> {
            var json = jsonString
            if (!jsonString.startsWith("{")) {
                json = "{$json"
            }
            if (!json.endsWith("}")) {
                json = "$json}"
            }
            val gson = Gson()
            (gson.fromJson(json, Map::class.java) as Map<String, String>).let {
                return it
            }
        }
    }
}
