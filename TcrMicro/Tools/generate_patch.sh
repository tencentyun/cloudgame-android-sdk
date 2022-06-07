#!/bin/bash

echo "### patch begin";
java -jar tinker-patch-cli.jar -new hotUpdate/new.apk -config tinker_config.xml -out patch
cp patch/patch_unsigned.apk hotUpdate/patch.apk
cp patch/log.txt hotUpdate/
rm -rf patch
echo "### patch end";
