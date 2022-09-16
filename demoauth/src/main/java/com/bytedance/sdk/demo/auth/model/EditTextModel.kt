package com.bytedance.sdk.demo.auth.model

enum class ContentType {
    GSON_ARRAY, GSON_OBJECT
}

data class EditTextModel(val title: String, val desc: String, val contentType: ContentType = ContentType.GSON_ARRAY, var editText: String? = null) : DataModel {
    override val viewType = ViewType.EDIT_TEXT

    fun gsonEditText(): String? {
        editText?.let {
            return when (contentType) {
                ContentType.GSON_ARRAY -> {
                    "[$it]"
                }
                ContentType.GSON_OBJECT -> {
                    "{$it}"
                }
            }
        }
        return null
    }
}
