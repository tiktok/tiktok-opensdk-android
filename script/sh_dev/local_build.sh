#!/usr/bin/env bash

##############################################################################
##
##  py构建3端 @leo.zhong
##  ./sh_dev/build_with_py.sh m
##  ./sh_dev/build_with_py.sh t
##  ./sh_dev/build_with_py.sh d
##
##############################################################################

build_m(){
   echo "start build musically"
   workpath=./app
   cd $workpath/
   file=./release.properties
   echo 'umeng_channels = local_test' > $file
   echo 'release_dir = release' >> $file
   echo 'check_lephone = yes' >> $file
   echo 'tweak_google = no' >> $file
   echo 'flavor = musically' >> $file
   echo 'special_channels=local_test,googleplay' >> $file
   mkdir -p release
   python2.7 ../tools/python_dev/build_new.py
}

build_t(){
   echo "start build tiktok"
   workpath=./app
   cd $workpath/
   file=./release.properties
   echo 'umeng_channels = local_test' > $file
   echo 'release_dir = release' >> $file
   echo 'check_lephone = yes' >> $file
   echo 'tweak_google = no' >> $file
   echo 'flavor = tiktok' >> $file
   echo 'special_channels=local_test,googleplay' >> $file
   mkdir -p release
   python2.7 ../tools/python_dev/build_new.py
}

build_d(){
   echo "start build douyin"
   workpath=./app
   cd $workpath/
   file=./release.properties
   echo 'umeng_channels = local_test' > $file
   echo 'release_dir = release' >> $file
   echo 'check_lephone = yes' >> $file
   echo 'tweak_google = no' >> $file
   echo 'flavor = douyin' >> $file
   mkdir -p release
   python2.7 ../tools/python_dev/build_new.py
}

if [ "$1" == "m" ]; then
    build_m
elif [ "$1" == "d" ]; then
    build_d
elif [ "$1" == "t" ]; then
    build_t
else
    echo "unknown type"
fi