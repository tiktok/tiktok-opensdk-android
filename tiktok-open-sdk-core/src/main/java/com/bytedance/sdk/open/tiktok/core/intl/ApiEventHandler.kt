package com.bytedance.sdk.open.tiktok.core.intl

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import com.bytedance.sdk.open.tiktok.core.model.Base

interface ApiEventHandler<in T : Base.Request, in V : Base.Response> {
    fun onRequest(req: T)

    fun onResponse(resp: V)
}
