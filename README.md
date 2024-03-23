# TikTok OpenSDK for Android

## Introduction
TikTok OpenSDK for Android is a Gradle project that allows you to integrate with native TikTok app functions. With OpenSDK, you can enable your users to log into your app with TikTok, and then share images and videos to their TikTok profiles.

## Getting Started
The minimum system requirement is API level of 21: Android 5.0 (Lollipop) or later.

### Step 1: Configure TikTok App Settings for Android
Register your app on the [TikTok for Developers website](https://developers.tiktok.com/login/).
You must provide your MD5 and SHA256 certificate fingerprint during the app registration process.
- If your app uses app signing by Google Play, copy the MD5 and SHA-256 fingerprint from the app signing page of the Play Console.
- If your app is using self-signing, you can follow the instructions below to use Keytool or Gradle's Signing Report to get your MD5 and SHA-256.

1. Using Keytool
Open a terminal and run the keytool utility to get the MD5 and SHA-256 fingerprint of the certificate.
```
keytool -list -v \
-alias <your-key-name> -keystore <path-to-production-keystore>

MD5: 75:C8:69:FC:D5:6E:EB:1D:02:79:A9:3F:91:BD:5E:5B
SHA1: EE:26:47:EC:59:83:6A:91:3C:7A:E1:61:14:56:6D:D8:90:B7:BA:3E
SHA-256: 6C:CF:15:C0:17:2E:EF:3E:48:2F:7E:E8:ED:6D:06:CB:CB:52:A4:CF:AD:CE:42:0B:80:9D:D5:D9:DE:DA:4C:7D

```
2. Using Gradle's Signing Report
```gradle
./gradlew signingReport
```

The signing report includes the signing information for each of your app's variants. Copy the MD5 and SHA-256 of the release variant.
```
Store: <your_keystore_location>
Alias: <your_keystore_alias>
MD5: 75:C8:69:FC:D5:6E:EB:1D:02:79:A9:3F:91:BD:5E:5B
SHA1: EE:26:47:EC:59:83:6A:91:3C:7A:E1:61:14:56:6D:D8:90:B7:BA:3E
SHA-256: 6C:CF:15:C0:17:2E:EF:3E:48:2F:7E:E8:ED:6D:06:CB:CB:52:A4:CF:AD:CE:42:0B:80:9D:D5:D9:DE:DA:4C:7D
```

Provide the MD5 and SHA-256 for your app on the TikTok for Developers website.
- In the App signature field, remove the colon from your MD5 string and provide the 32-character signature.
- In the Signing certificate fingerprints field, provide your SHA-256 string directly.

### Step 2: Install the SDK and Setup Android Project
1. In the Project window, switch to the Android view tab.
2. Open Gradle Scripts and locate `build.gradle` (Project).
3. Add the following repository in the repositories{} section:
```gradle
repositories {
    maven { url "https://artifact.bytedance.com/repository/AwemeOpenSDK" }
}
```

4. Open Gradle Scripts and locate `build.gradle` (Module: app).
5. Add the following implementation statement to the `dependencies{}` section:
```gradle
dependencies {
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk-core:2.3.0'
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk-auth:2.3.0'   // to use authorization api
    implementation 'com.tiktok.open.sdk:tiktok-open-sdk-share:2.3.0'    // to use share api
}
```

> Note:
Due to changes in Android 11 regarding package visibility, when implementing Tiktok SDK for devices targeting Android 11 and higher, add the following to the Android Manifest file.
```xml
<queries>
    <package android:name="com.zhiliaoapp.musically" />
    <package android:name="com.ss.android.ugc.trill" />
</queries>
```

Sync your project and get the latest version of SDK package.
At this point, the basic development environment setup is complete.

## Security

If you discover a potential security issue in this project, or think you may have discovered a security issue, please notify TikTok via our [Bug Bounty Program](https://hackerone.com/tiktok).
Please do NOT create a public GitHub issue.

## License
This source code is licensed under the license found in the LICENSE file in the root directory of this source tree.