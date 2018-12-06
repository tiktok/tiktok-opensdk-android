
rm -rf apk.zip
echo $WORKSPACE
cd $WORKSPACE
#git reset --hard
rm -rf aweme/mbak-src/
rm -rf gradle-pushbak.properties

source /Users/ies_mini_2/.bash_profile
git checkout master
git pull
#git submodule update --init

#zip打包模式
#cp -r -f /Users/ugc_test/jenkins/workspace/aweme_android_master/mapping/finalize_apk_zip.py /Users/ugc_test/jenkins/workspace/aweme_android_master/misc/finalize_apk.py

cd $WORKSPACE/aweme/

#channels="umeng_channels = local_test"
#echo $channels
file=$WORKSPACE/aweme/release.properties
#echo $channels > $file
#echo 'release_dir = release' >> $file
#echo 'check_lephone = yes' >> $file
#echo 'tweak_google = no' >> $file
echo "start build apk"

#ls -l
ANDROID=/Users/ies_mini_2/Library/Android
#GRADLE_HOME=$ANDROID/gradle-2.7
ANDROID_SDK=$ANDROID/sdk
ANDROID_HOME=$ANDROID/sdk
export ANDROID_HOME
export ANDROID_SDK
export PATH=$PATH:$GRADLE_HOME/bin:$ANDROID/sdk/platform-tools:$ANDROID/sdk/tools
export ANDROID
#export GRADLE_HOME

echo $PATH
mkdir -p release
DIRECTORY=$WORKSPACE/aweme/release/
if [ "`ls -A $DIRECTORY`" = "" ]; then
	echo "$DIRECTORY is indeed empty"
else
	echo "$DIRECTORY is not empty"
	rm -rf ./release/*
fi

#cp -r /Users/ugc_test/jenkins/workspace/build RNBundle/node_modules/react-native/ReactAndroid/src/main/java/com/facebook/react/common/

cp -r -f ../release.properties_normal ./release.properties
python ../python_dev/build_new.py
cp -r -f ../release.properties_google ./release.properties
python ../python_dev/build_new.py

#
#BUILD_RESULT=0
#if [ -d "$DIRECTORY" ]; then
#  for APK_FILE in `find ${DIRECTORY} -name "*.apk"`
#  do
#    BUILD_RESULT=1
#    APK_NAME=$(echo ${APK_FILE##*/})
#    echo $APK_FILE
#    break
#  done
#
#  if [ ${BUILD_RESULT} -eq 1 ]; then
#  	mv ./release/*.apk ./release/Aweme-release.apk
#    echo "APK_NAME:"${APK_NAME}
#  	echo "BUILD SUCCEED"
#  else
#  	echo "BUILD FAILURE"
#  fi
#else
#  echo "BUILD FAILURE"
#fi

#修正工作目录下所有apk，去除_master_new
for file in `find ${DIRECTORY} -name "*.apk"`
do
	new_file=$(echo $file | sed "s|_android_master_new_|_|")
	mv $file $new_file
done

echo "----------------------------------All  done --------------------------------"
echo "----------------------------------All  done --------------------------------"
echo "----------------------------------All  done --------------------------------"
echo "----------------------------------All  done --------------------------------"
echo "----------------------------------All  done --------------------------------"
echo "----------------------------------All  done --------------------------------"
echo "----------------------------------All  done --------------------------------"
echo "----------------------------------All  done --------------------------------"
echo "----------------------------------All  done --------------------------------"

