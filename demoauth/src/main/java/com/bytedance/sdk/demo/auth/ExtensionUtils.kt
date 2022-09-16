package com.bytedance.sdk.demo.auth

import java.lang.StringBuilder

fun List<String>.joinBy(separator: String): String {
    val sb = StringBuilder()
    for (i in 0 until this.size) {
        if (i > 0) {
            sb.append(separator)
        }
        sb.append(this[i])
    }
    return sb.toString()
}
