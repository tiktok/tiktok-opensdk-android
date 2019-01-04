# Android接入指南
注：本文为抖音短视频 Android终端SDK新手使用教程，只涉及SDK的使用方法，默认读者已经具有使用Android Studio开发Android程序的经验，以及相关的编程知识基础等。

## Requirements
* SDK最低支持：Android API 16 - 4.1.x版本

## 第一步：向抖音短视频申请你的clientkey
> 请到[开发者应用登记页面]()进行申请，申请后将获得clientkey，审核通过的应用可使用clientkey和抖音短视频通信。
> 抖音授权SDK基于OAuth2.0授权，让抖音用户使用抖音身份安全登录第三方App，在抖音用户授权登录已经接入授权SDK的第三方App后，第三方App可以获得用户的接口授权码（authCode），通过authCode可以进行抖音授权接口的调用，从而获得抖音用户的基本开放信息等。

## 第二步：下载Android Demo
点击下载 [抖音短视频授权Demo]()。

“抖音短视频授权DEMO”提供了完整的工程配置及接入代码，你只需将其中的clientkey替换为你申请的clientkey，将local.properties中的sdk.dir配置为你的运行环境sdk位置，即可体验功能并供开发参考。

## 第三步：集成到开发环境
1. 在工程根目录build.gradle中添加头条的maven仓库地址

```
maven { url "https://maven.byted.org/nexus/content/repositories/ugc_android/" }
maven { url 'https://maven.byted.org/nexus/content/repositories/ss_app_android/' }
```

2. 在app的根目录build.gradle中添加依赖

```
compile "com.bytedance.ies.ugc.aweme:aweme-open-sdk:xx"
```

> 说明：当前推荐版本号：0.0.0.8

## 使用SDK进行授权
1. 在Application中，初始化TTOpenApiFactory

```
String clientkey = "XXXXX"; // 修改为在开发者应用登记页面申请的clientkey
TTOpenApiFactory.init(new BDOpenConfig(clientkey));
```

2. 请求授权

```
SendAuth.Request request = new SendAuth.Request();
request.scope = "user_info";                             // 用户授权时必选权限
request.state = "ww";                                    // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
TTOpenApi ttOpenApi= TTOpenApiFactory.create(this);
ttOpenApi.sendAuthLogin(request); //请求授权。如果没有安装应用。使用h5页面授权
```

3. 接受返回信息

> 包名下创建bdopen.BdEntryActivity，初始化TTOpenApi，实现BDApiEventHandler接口，在onResp方法中回调授权结果。

```
public class BdEntryActivity extends Activity implements BDApiEventHandler {

    TTOpenApi ttOpenApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi= TTOpenApiFactory.create(this);
        ttOpenApi.handleIntent(getIntent(),this);
    }

    @Override
    public void onResp(BaseResp resp) {
        // 授权成功可以获得authCode
        SendAuth.Response response = (SendAuth.Response) resp;
        log("authCode " + response.authCode);        
    }
}
```

4. Manifest中注册

```
 <uses-permission android:name="android.permission.INTERNET" />
```

```
<activity
    android:name=".bdopen.BdEntryActivity"
    android:exported="true">
</activity>
```

## 常见问题
1. 内嵌到其他app的sdk接入时，为什么在包名下创建bdopen.BdEntryActivity不会被吊起？
    答：请求授权时，​request.callerLocalEntry​设置接收回调类的全路径名。
2. 为什么接入授权sdk后，网页授权界面空白？
   答：抖音的授权页面使用https，你需要配置你的网络接受https的证书。可以像demo manifest文件中，通过android:networkSecurityConfig设置。
