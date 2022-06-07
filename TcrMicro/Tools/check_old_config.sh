#!/bin/bash

echo "### begin check"
java -jar tinker-patch-cli.jar -old hotUpdate/old.apk -patch hotUpdate/patch.apk -config tinker_config.xml -out check_result
echo "### end check"
