#!/usr/bin/env bash

# 参数1：china or oversea
# 参数2：internal or external
# 参数3：true or false，是否打snapshot

#exist when error occur
set -e

# fail on error, verbose output
set -exo pipefail

git fetch

current_branch=$(git rev-parse --abbrev-ref HEAD)
module="opentt"
area=China
artifactId=aweme-open-sdk
mode=Internal
if [[ $current_branch != develop && $current_branch != master ]];then
    snapshot=True
else
    snapshot=False
fi
for i in $*
do
    case "$i" in
            china)
                area=China
                artifactId=aweme-open-sdk
            ;;
            overseas)
                area=Overseas
                artifactId=aweme-open-sdk-overseas
            ;;
            internal)
                mode=Internal
            ;;
            external)
                mode=External
            ;;
            true)
                snapshot=True
            ;;
            false)
                snapshot=False
            ;;
        esac
done

uncommit=$(git status | grep -iE "new\ file|modified")
unpush=$(git status | grep -i "git\ push")
if [ -n "$uncommit" ];then
    echo "There are changes not staged for commit"
    exit 1
fi
if [ -n "$unpush" ];then
    echo "There are commits not pushed"
    exit 1
fi


#确定对应版本的version
if [ "$area" == "china" ]; then
    version=$(git show $branch:$module/gradle.properties | awk '/ARTIFACT_CHINAL_VERSION/')
else
    version=$(git show $branch:$module/gradle.properties | awk '/ARTIFACT_OVERSEAS_VERSION/')
fi

echo "branch:$current_branch, area:$area, mode:$mode, snapshot:$snapshot version:$version"

if [[ $snapshot == True ]];then
    # 如果是snapshot版本，输出版本号
    ./gradlew publish${area}${mode}AARPublicationToMavenRepository -Psnapshot=true 1> publish_cache
    build_result=$(grep -iE "BUILD SUCCESSFUL" ./publish_cache)
    if [ -n "$build_result" ];then
        # 如果成功的话，输出版本
        upload_aar_record=$(grep -E "^Upload https.+aar$" ./publish_cache)
        snapshot_version=${upload_aar_record:0-30:26}
    else
        echo "build failed."
    fi
    rm -rf ./publish_cache

    echo "The snapshot version is $artifactId:$snapshot_version"
else
    # TODO： 如果是出正式版，则升级一位版本号，然后成功后，打tag

    ./gradlew publish${area}${mode}AARPublicationToMavenRepository -Psnapshot=false >> publish_cache
fi