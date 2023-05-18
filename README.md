# TikTok OpenSDK for Android

## Introduction
TikTok OpenSDK for Android is a Gradle project that allows you to integrate with native TikTok app functions. With OpenSDK, you can enable your users to log into your app with TikTok, and then share images and videos to their TikTok profiles.

## Get Started
The minimum system requirement is API level of 21: Android 5.0 (Lollipop) or later.

### Step 1: Configure TikTok App Settings for Android
Register your app on the [TikTok for Developers website](https://developers.tiktok.com/login/).
Upon app approval, you will be provided client_key and client_secret, which are required for later steps.

### Step 2: Install the SDK and Setup Android Project
1. In the Project window, switch to the Android view tab.
2. Open Gradle Scripts and locate `build.gradle` (Project).
3. Add the following repository in the repositories{} section:
```gradle
repositories {
    maven { url "https://artifact.bytedance.com/repository/AwemeOpenSDK" }
}
```

4. Open Gradle Scripts and locate `build.gradle` (Module: your_app).
5. Add the following implementation statement to the `dependencies{}` section:
```gradle
dependencies {
    implementation 'com.bytedance.ies.ugc.aweme:tiktok-open-sdk-core:1.0.0'
    implementation 'com.bytedance.ies.ugc.aweme:tiktok-open-sdk-auth:1.0.0'   // to use authorization api
    implementation 'com.bytedance.ies.ugc.aweme:tiktok-open-sdk-share:1.0.0'    // to use share api
}
```

To use Auth sdk api, you can initialize an `AuthApi` object with your client key and customized API event handler. Please refer to `MainActivity` in `demo-auth` package as an example.
```kotlin
authApi = AuthApi(
    context = [your_activity_or_application_context],
    clientKey = [your_client_key],
    apiEventHandler = [your_api_event_handler]
)
authApi.authorize(
    request = request,
    authMethod = AuthMethod.TikTokApp / AuthMethod.Browser
)

// use authApi to handle result intent in activity
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    authApi.handleResultIntent(intent)
}

```

To use Share sdk api, you can initialize a `ShareApi` object with your client key and customized API event handler. Please refer to `ShareActivity` in demo-share package as an example.
```kotlin
shareApi = ShareApi(
    context = [your_activity_or_application_context],
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
If your app users encounter a frozen screen after sharing a video to TikTok, they must manually grant permission to files and media to TikTok app, or update their TikTok app to v26.7.0 or later. See [Google Play Help](https://support.google.com/googleplay/answer/9431959?hl=en) to learn how to change app permissions on your Android device.
Sync your project and get the latest version of SDK package.
At this point, the basic development environment setup is complete.

## Security

If you discover a potential security issue in this project, or think you may have discovered a security issue, please notify Bytedance via our [feedback form](https://developers.tiktok.com/support/).
Please do NOT create a public GitHub issue.
