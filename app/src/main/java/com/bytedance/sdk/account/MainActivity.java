package com.bytedance.sdk.account;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.bytedance.sdk.account.common.model.SendAuth;
import com.bytedance.sdk.account.open.aweme.api.TTOpenApi;
import com.bytedance.sdk.account.open.aweme.impl.TTOpenApiFactory;

public class MainActivity extends AppCompatActivity {

    TTOpenApi bdOpenApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bdOpenApi = TTOpenApiFactory.create(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // 调用wap授权预加载
        SendAuth.Request request = new SendAuth.Request();
        request.scope = "user_info,friend_relation,message";
        request.optionalScope1 = "friend_relation,message";
        request.state = "ww";
        request.wapRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        // wap预加载接口，需要和sendAuthLogin或者sendInnerWebAuthRequest使用配置相同的SendAuth.Request，但不需要是同一实例
        bdOpenApi.preloadWebAuth(request);

        findViewById(R.id.go_to_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果本地未安装抖音或者抖音的版本过低，会直接自动调用 web页面 进行授权
                sendAuth(false);
            }
        });

        findViewById(R.id.go_to_auth_just_through_web).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAuth(true);
            }
        });
    }

    private boolean sendAuth(boolean isWebAuth) {
        SendAuth.Request request = new SendAuth.Request();
        request.scope = "user_info";                            // 用户授权时必选权限
        request.optionalScope1 = "friend_relation,message";     // 用户授权时可选权限（默认不选）
        request.state = "ww";                                   // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
        request.wapRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;     // 指定wap授权页横竖屏展示，不指定时由系统控制
        if (isWebAuth) {
            return bdOpenApi.sendInnerWebAuthRequest(request);     // 打开wap授权页进行授权
        } else {
            return bdOpenApi.sendAuthLogin(request);               // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用wap页授权
        }
    }


}
