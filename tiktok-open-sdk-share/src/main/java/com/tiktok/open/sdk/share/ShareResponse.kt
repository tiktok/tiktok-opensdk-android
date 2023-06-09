package com.tiktok.open.sdk.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.os.Bundle
import com.tiktok.open.sdk.core.model.Base
import com.tiktok.open.sdk.share.constants.Constants
import com.tiktok.open.sdk.share.constants.Keys

/*
   * Share.Response
   *
   * @param state the sharing state.
   * @param errorCode the error code for sharing result .
   * @param subErrorCode the code which provides more detail information.
   * @param errorMsg the error message
   * @param extras the extra information
 */
data class ShareResponse(
    val state: String?,
    override val errorCode: Int,
    val subErrorCode: Int?,
    override val errorMsg: String?,
    override val extras: Bundle? = null,
) : Base.Response() {
    override val type: Int = Constants.SHARE_RESPONSE
}

internal fun Bundle.toShareResponse(): ShareResponse {
    val state = getString(Keys.Share.STATE)
    val subErrorCode = getInt(Keys.Share.SHARE_SUB_ERROR_CODE)
    val errorCode = getInt(Keys.Share.ERROR_CODE)
    val errorMsg = getString(Keys.Share.ERROR_MSG)
    val extras = getBundle(com.tiktok.open.sdk.core.constants.Keys.Base.EXTRA)
    return ShareResponse(
        state = state,
        subErrorCode = subErrorCode,
        errorCode = errorCode,
        errorMsg = errorMsg,
        extras = extras
    )
}
