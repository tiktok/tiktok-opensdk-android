package com.bytedance.sdk.demo.auth.model

enum class ViewType(val value: Int) {
    SCOPE(0), LOGO(1), HEADER(2), EDIT_TEXT(3), CONFIG(4);
    companion object {
        fun typeFrom(value: Int): ViewType {
            return when (value) {
                1 -> { LOGO }
                2 -> { HEADER }
                3 -> { EDIT_TEXT }
                4 -> { CONFIG }
                else -> { SCOPE }
            }
        }
    }
}

interface DataModel {
    val viewType: ViewType
}