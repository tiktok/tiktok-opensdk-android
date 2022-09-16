package com.bytedance.sdk.demo.share.model

import androidx.lifecycle.MutableLiveData

class LogoModel(val image: MutableLiveData<String> = MutableLiveData()) : DataModel {
    override val viewType = ViewType.LOGO
}
