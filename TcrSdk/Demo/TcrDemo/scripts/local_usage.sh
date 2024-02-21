set -e

echo -e "apply from: './gradle/config.gradle'" >>  ../build.gradle

echo -e "include ':TcrSdkFull'" >>  ../settings.gradle
## 这里将SDK本地项目路径替换到local full path这里
echo -e "project(':TcrSdkFull').projectDir = new File('local full path')" >>  ../settings.gradle

cd ../app
ls
# app依赖替换
sed -i '' 's/implementation "com.tencent.tcr:tcrsdk-full:3.17.3"/implementation project(path: \x27:TcrSdkFull\x27)/g' build.gradle








