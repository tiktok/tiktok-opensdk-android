package com.bytedance.sdk.account.bdopen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;
import com.bytedance.sdk.account.open.aweme.impl.TTOpenApiFactory;

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
        ttOpenApi.handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        // 授权成功可以获得authCode
        SendAuth.Response response = (SendAuth.Response) resp;
        Log.d("AuthResultTest","authCode " + response.authCode);
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
    }
}
