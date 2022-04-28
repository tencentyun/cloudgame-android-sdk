#!/bin/bash
if [ $# -eq 0 ];
then
    echo "### You must use ./apply_patch.sh debug  or ./apply_patch.sh release"
    exit 1
fi

# 该脚本在微端示例工程根目录运行
# 修改为微端包的工程名
OLD_PROJECT="TcrMicroAppForUnity2018Empty"
# 修改为更新包的工程名，同时将更新包放到和微端包同级目录
NEW_PROJECT="AndroidUnity2018Empty"

if [ "$1" = "debug" ]
then
    echo "### debug patch begin";
    cd ../$NEW_PROJECT || exit
    echo "### $NEW_PROJECT assembleDebug";
    ./gradlew assembleDebug

    cd ../$OLD_PROJECT || exit
    rm -rf hotUpdate
    mkdir hotUpdate
    chmod -R +x hotUpdate
    echo "### copy hotUpdate apk and R";
    cp ../$NEW_PROJECT/build/outputs/apk/debug/AndroidUnity2018Empty-debug.apk hotUpdate/new.apk
    cp ../$NEW_PROJECT/build/intermediates/runtime_symbol_list/debug/R.txt hotUpdate/new-R.txt

    echo "### $OLD_PROJECT assembleDebug";
    ./gradlew assembleDebug

    cp app/build/outputs/apk/debug/*.apk hotUpdate/old.apk
    echo "### begin patch";
    java -jar tools/tinker-patch-cli-1.9.14.18.jar -old hotUpdate/old.apk -new hotUpdate/new.apk -config tools/tinker_config.xml -out patch_debug
    cp patch_debug/patch_signed.apk hotUpdate/
    cp patch_debug/log.txt hotUpdate/
    rm -rf patch_debug
else
    echo "release patch begin"
    cd ../$NEW_PROJECT || exit
    echo "### $OLD_PROJECT assembleRelease";
    ./gradlew assembleRelease

    cd ../$OLD_PROJECT || exit
    rm -rf hotUpdate
    mkdir hotUpdate
    chmod -R +x hotUpdate
    echo "### copy hotUpdate apk and R";
    cp ../$NEW_PROJECT/build/outputs/apk/release/AndroidUnity2018Empty-release.apk hotUpdate/new.apk
    cp ../$NEW_PROJECT/build/intermediates/runtime_symbol_list/release/R.txt hotUpdate/new-R.txt

    echo "### $OLD_PROJECT assembleRelease";
    ./gradlew assembleRelease

    cp app/build/outputs/apk/release/*.apk hotUpdate/old.apk
    echo "### begin patch";
    java -jar tools/tinker-patch-cli-1.9.14.18.jar -old hotUpdate/old.apk -new hotUpdate/new.apk -config tools/tinker_config.xml -out patch_release
    cp patch_release/patch_signed.apk hotUpdate/
    cp patch_release/log.txt hotUpdate/
    rm -rf patch_release
fi

echo "try create /storage/emulated/0/Download folder"
# WeTest上的设备启动时Download目录不存在, 需要提前创建否则push不上去
adb shell mkdir -p /storage/emulated/0/Download
echo "begin push into /storage/emulated/0/Download"
adb push hotUpdate/patch_signed.apk /storage/emulated/0/Download/
echo "begin uninstall com.DefaultCompany.Unity2018Empty"
adb uninstall com.DefaultCompany.Unity2018Empty
echo "begin install old.apk"
adb install -r -d hotUpdate/old.apk
