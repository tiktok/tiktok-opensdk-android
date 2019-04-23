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




#确定对应版本的version
if ($1 == china); then
    version=$(git show $branch:$module/gradle.properties | awk '/ARTIFACT_CHINAL_VERSION/')
else
    version=$(git show $branch:$module/gradle.properties | awk '/ARTIFACT_OVERSEAS_VERSION/')
fi

echo "version:$version"