#!/bin/bash
# author: liang.tong@bytedance.com

cd $(dirname $0)
cd ../../

function showHelp() {
    echo "抖音product: ./install.sh d"
    echo "Musical.ly: ./install.sh m"
    echo "TikTok: ./install.sh t"
}

if [ -z $1 ];then
    showHelp
    exit -1
fi

if [ $1 == 'd' ];then
    echo "抖音安装中...."
    adb install -r app/build/outputs/apk/DouyinCn/debug/app-douyin-cn-debug.apk
    adb shell am start com.ss.android.ugc.aweme/.splash.SplashActivity
elif [ $1 == 'm' ];then
    echo "Musical.ly安装中...."
    adb install -r app/build/outputs/apk/MusicallyI18n/debug/app-musically-i18n-debug.apk
    adb shell am start com.zhiliaoapp.musically/com.ss.android.ugc.aweme.splash.SplashActivity
elif [ $1 == 't' ];then
    echo "TikTok安装中...."
    adb install -r app/build/outputs/apk/TikTokI18n/debug/app-tiktok-i18n-debug.apk
    adb shell am start com.ss.android.ugc.trill/com.ss.android.ugc.aweme.splash.SplashActivity
else
    showHelp
fi

