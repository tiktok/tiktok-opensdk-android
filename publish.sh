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
area=china
mode=internal
snapshot=True
for i in $*
do
    case "$i" in
            china)
                area=china
            ;;
            overseas)
                area=overseas
            ;;
            internal)
                mode=internal
            ;;
            external)
                mode=external
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
#if [ -n "$uncommit" ];then
#    echo "There are changes not staged for commit"
#    exit 1
#fi
#if [ -n "$unpush" ];then
#    echo "There are commits not pushed"
#    exit 1
#fi


#确定对应版本的version
if [ "$area" == "china" ]; then
    version=$(git show $branch:$module/gradle.properties | awk '/ARTIFACT_CHINAL_VERSION/')
else
    version=$(git show $branch:$module/gradle.properties | awk '/ARTIFACT_OVERSEAS_VERSION/')
fi

echo "branch:$current_branch, area:$area, mode:$mode, snapshot:$snapshot version:$version"

# 打包上传
#./gradlew