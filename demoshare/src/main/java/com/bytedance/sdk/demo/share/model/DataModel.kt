package com.bytedance.sdk.demo.share.model

enum class ViewType(val value: Int) {
    TOGGLE(0), LOGO(1), HEADER(2), EDIT_TEXT(3), INFO(4);
    companion object {
        fun typeFrom(value: Int): ViewType {
            return when (value) {
                1 -> { LOGO }
                2 -> { HEADER }
                3 -> { EDIT_TEXT }
                4 -> { INFO }
                else -> { TOGGLE }
            }
        }
    }
}

interface DataModel {
    val viewType: ViewType
}
