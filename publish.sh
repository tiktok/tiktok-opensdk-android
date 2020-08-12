#!/usr/bin/env bash

# Parameter 1：internal or external
# Parameter 2：true or false，enable snapshot

#exist when error occur
set -e

# fail on error, verbose output
set -exo pipefail

git fetch

current_branch=$(git rev-parse --abbrev-ref HEAD)
module="openoversea"
area=Overseas
artifactId=aweme-open-sdk-overseas
mode=Internal
if [[ $current_branch != develop && $current_branch != master ]];then
    snapshot=True
else
    snapshot=False
fi
for i in $*
do
    case "$i" in
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

if [ -n "$(git status | grep -iE "new\ file|modified")" ];then
    echo "There are changes not staged for commit"
    exit 1
fi
if [ -n "$(git status | grep -i "git\ push")" ];then
    echo "There are commits not pushed"
    exit 1
fi

#确定对应版本的version
version=$(git show $branch:$module/gradle.properties | awk '/ARTIFACT_OVERSEAS_VERSION/')

echo "branch:$current_branch, mode:$mode, snapshot:$snapshot version:$version"

if [[ $snapshot == True ]];then
    # 如果是snapshot版本，输出版本号
    # If this is a snapshot，add a version number
    ./gradlew publish${area}${mode}AARPublicationToMavenRepository -Psnapshot=true 1> publish_cache
    build_result=$(grep -iE "BUILD SUCCESSFUL" ./publish_cache)
    if [ -n "$build_result" ];then
        # 如果成功的话，输出版本
        # If successful, then output the version
        upload_aar_record=$(grep -E "^Upload https.+aar$" ./publish_cache)
        snapshot_version=${upload_aar_record:0-30:26}
    else
        echo "build failed."
    fi
    rm -rf ./publish_cache

    echo "The snapshot version is $artifactId:$snapshot_version"
else
    # TODO： 如果是出正式版，则升级一位版本号，然后成功后，打tag
    # TODO: If releasing an official version, upgrade the tag after a successful upload
    ./gradlew publish${area}${mode}AARPublicationToMavenRepository -Psnapshot=false
fi