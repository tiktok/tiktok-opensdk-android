package com.bytedance.sdk.account.bdopen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.constants.BDOpenConstants;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.DYOpenConstants;
import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;
import com.bytedance.sdk.account.open.aweme.impl.TTOpenApiFactory;
import com.bytedance.sdk.account.open.aweme.share.Share;

/**
 * 主要功能：接受授权返回结果的activity
 *
 * 注：该activity必须在程序包名下 bdopen包下定义
 * since: 2018/12/25
 */
public class BdEntryActivity extends Activity implements BDApiEventHandler {

    TTOpenApi ttOpenApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi= TTOpenApiFactory.create(this);

        //auth使用handleIntent，share使用handleShareIntent
        //ttOpenApi.handleIntent(getIntent(),this);
        ttOpenApi.handleShareIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == BDOpenConstants.ModeType.SEND_AUTH_RESPONSE) {
            // 授权成功可以获得authCode
            SendAuth.Response response = (SendAuth.Response) resp;
            Log.d("AuthResultTest","authCode " + response.authCode);
        } else if (resp.getType() == DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY_RESP) {
            Share.Response response = (Share.Response) resp;
        }
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
    }
}
