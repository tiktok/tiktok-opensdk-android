package com.bytedance.sdk.open.tiktok.auth

import com.bytedance.sdk.open.tiktok.core.intl.ApiEventHandler

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

interface AuthApiEventHandler : ApiEventHandler<Auth.Request, Auth.Response>
