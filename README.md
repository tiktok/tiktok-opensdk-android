# TikTok OpenSDK for Android

## TikTok OpenSDK Features

TikTok Android OpenSDK is a gradle project to seamlessly work with native TikTok app on functions such as authorization and video/image sharing. 
There will more features to help partners, developers and TikTok user experience.

## Get Started
You should confirm that your project has a Minimum API level of 16: Android 4.1 (Jelly Bean) or higher.

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
    implementation 'com.bytedance.ies.ugc.aweme:opensdk-oversea-external:0.2.1.1'
}
```

3. Edit your Application

First you need to initialize TikTokOpenApiFactory by using client key in your custom Application.
```kotlin
@Override
public void onCreate() {
    super.onCreate();
    String clientKey = "[CLIENT_KEY]";
    TikTokOpenConfig tiktokOpenConfig = new TikTokOpenConfig(clientKey);
    TikTokOpenApiFactory.init(new TikTokOpenConfig(tiktokOpenConfig));
}
```

4. Edit Your Manifest

Open the `/app/manifest/AndroidManifest.xml` file.
Register `TikTokEntryActivity` for receiving callbacks in Manifest. If you have customized an activity to receive callbacks, you may skip this step.
```xml
// If you have customized activity to receive callbacks, you can skip the step
<activity
    android:name=".tiktokapi.TikTokEntryActivity"
    android:exported="true">
</activity>
```

> Note:
Due to changes in Android 11 regarding package visibility, when impementing Tiktok SDK for devices targeting Android 11 and higher, add the following to the Android Manifest file:
```xml
<queries>
    <package android:name="com.zhiliaoapp.musically" />
    <package android:name="com.ss.android.ugc.trill" />
</queries>
```
Sync your project and get the latest version of SDK package.
At this point, you should already set up the basic development environment.

## Contribution (remove?)

Please check [Contributing](CONTRIBUTING.md) for more details.

## Code of Conduct (remove?)

Please check [Code of Conduct](CODE_OF_CONDUCT.md) for more details.

## Security

If you discover a potential security issue in this project, or think you may
have discovered a security issue, we ask that you notify Bytedance Security via our [security center](https://security.bytedance.com/src) or [vulnerability reporting email](sec@bytedance.com).

Please do **not** create a public GitHub issue.

## License

This project is licensed under the [Apache-2.0 License](LICENSE).