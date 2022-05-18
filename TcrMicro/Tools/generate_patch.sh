#!/bin/bash

echo "### patch begin";
java -jar tinker-patch-cli-1.9.14.18.jar -old hotUpdate/old.apk -new hotUpdate/new.apk -config tinker_config.xml -out patch
cp patch/patch_unsigned.apk hotUpdate/patch.apk
cp patch/log.txt hotUpdate/
rm -rf patch
