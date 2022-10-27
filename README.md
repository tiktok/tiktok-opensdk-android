# TikTok OpenSDK for Android

## Introduction

TikTok Android OpenSDK is a gradle project to seamlessly work with native TikTok app on functions such as authorization and video/image sharing.
There will more features to help partners, developers and TikTok user experience.

## Get Started
You should confirm that your project has a Minimum API level of 21: Android 5.0 (Lollipop) or higher.

### Step 1: Configure TikTok App Settings for Android
Use the [Developer Portal](https://developers.tiktok.com/login/) to apply for Android `client_key` and `client_secret` access. Upon application approval, the Developer Portal will provide access to these keys.

### Step 2: Install the SDK and Setup Android Project
1. In Project window, switch to `Android` view tab and open Gradle Scripts > build.gradle (Project). Then add the following repository in the repositories{} section. For example:
```gradle
repositories {
    maven { url "https://artifact.bytedance.com/repository/AwemeOpenSDK" }
}
```

2. Open Gradle Scripts > build.gradle (Module: app) and add the following implementation statement to the dependencies{} section: {TODO: update location and version}
```gradle
dependencies {
    implementation 'com.bytedance.ies.ugc.aweme:tiktok-core:1.0.0'
    implementation 'com.bytedance.ies.ugc.aweme:tiktok-auth:1.0.0'   // to use authorization api
    implementation 'com.bytedance.ies.ugc.aweme:tiktok-share:1.0.0'    // to use share api
}
```

3. Edit your Application

To use auth sdk api, you can initialize an AuthApi object with your client key and customized api event handler, checkout MainActivity in demo-auth package
```kotlin
authApi = AuthApi(
    context = this,
    clientKey = [your_client_key],
    apiEventHandler = [your_api_event_handler]
)
authApi.authorize(
    request = request,
    useWebAuth = true / false
)

// use authApi to handle result intent in activity
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    authApi.handleResultIntent(intent)
}

```

To use share sdk api, you can initialize an ShareApi object with your client key and customized api event handler, checkout ShareActivity in demo-share package
```kotlin
shareApi = ShareApi(
    context = this,
    clientKey = [your_client_key],
    apiEventHandler = [your_api_event_handler]
)
shareApi.share(
    request = request,
)

// use shareApi to handle result intent in activity
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    shareApi.handleResultIntent(intent)
}

```

> Note:
Due to changes in Android 11 regarding package visibility, when implementing Tiktok SDK for devices targeting Android 11 and higher, add the following to the Android Manifest file:
```xml
<queries>
    <package android:name="com.zhiliaoapp.musically" />
    <package android:name="com.ss.android.ugc.trill" />
</queries>
```
If users face the issue that TikTok is stuck on splash screen after sharing to TikTok, please ask users to manually grant Files and Media Permission to TikTok(https://support.google.com/googleplay/answer/9431959?hl=en) OR update their TikTok apps to version v26.7.0 or above.

Sync your project and get the latest version of SDK package.
At this point, you should already set up the basic development environment.

## Security

If you discover a potential security issue in this project, or think you may
have discovered a security issue, we ask that you notify Bytedance Security via our [security center](https://security.bytedance.com/src) or [vulnerability reporting email](sec@bytedance.com).

Please do **not** create a public GitHub issue.

## License

This project is licensed under the [Apache-2.0 License](LICENSE).