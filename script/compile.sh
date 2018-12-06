#!/bin/bash
# author: liang.tong@bytedance.com

cd $(dirname $0)
cd ../

function showHelp() {
    echo "抖音product: ./compile.sh d"
    echo "Musical.ly: ./compile.sh m"
    echo "TikTok: ./compile.sh t"
    echo "上述命令均支持一位gradle编译标准参数，比如在后面加--offline进行离线编译"
}

if [ -z $1 ];then
    showHelp
    exit -1
fi

if [ $1 == 'd' ];then
    echo "抖音编译中...."
    if ./gradlew :app:assembleDouyinCnDebug $2; then
        adb install -r app/build/outputs/apk/DouyinCn/debug/app-douyin-cn-debug.apk
        adb shell am start com.ss.android.ugc.aweme/.splash.SplashActivity
    else
        echo "抖音编译失败!"
    fi
elif [ $1 == 'm' ];then
    echo "Musical.ly编译中...."
    if ./gradlew :app:assembleMusicallyI18nDebug $2; then
        adb install -r app/build/outputs/apk/MusicallyI18n/debug/app-musically-i18n-debug.apk
        adb shell am start com.zhiliaoapp.musically/com.ss.android.ugc.aweme.splash.SplashActivity
    else
        echo "Musical.ly编译失败!"
    fi
elif [ $1 == 't' ];then
    echo "TikTok编译中...."
    if ./gradlew :app:assembleTikTokI18nDebug $2; then
        adb install -r app/build/outputs/apk/TikTokI18n/debug/app-tiktok-i18n-debug.apk
        adb shell am start com.ss.android.ugc.trill/com.ss.android.ugc.aweme.splash.SplashActivity
    else
        echo "TikTok编译失败!"
    fi
else
    showHelp
fi
