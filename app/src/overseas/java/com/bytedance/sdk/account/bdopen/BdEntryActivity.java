package com.bytedance.sdk.account.bdopen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.account.MainActivity;
import com.bytedance.sdk.account.UserInfoActivity;
import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.constants.BDOpenConstants;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.account.open.aweme.impl.TikTokOpenApiFactory;

/**
 * 主要功能：接受授权返回结果的activity
 *
 * 注：该activity必须在程序包名下 bdopen包下定义
 * since: 2018/12/25
 */
public class BdEntryActivity extends Activity implements BDApiEventHandler {

    TiktokOpenApi ttOpenApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi= TikTokOpenApiFactory.create(this);
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
        Intent intent = null;
        if (resp.isSuccess()) {
            Toast.makeText(this, "授权成功，获得权限："+response.grantedPermissions, Toast.LENGTH_LONG).show();
            intent = new Intent(this, UserInfoActivity.class);
            intent.putExtra(MainActivity.CODE_KEY, response.authCode);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "授权失败", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
        Toast.makeText(this, "intent出错啦", Toast.LENGTH_LONG).show();
        finish();
    }
}
