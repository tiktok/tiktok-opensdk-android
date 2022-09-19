package com.bytedance.sdk.demo.auth.model

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
