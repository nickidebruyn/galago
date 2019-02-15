ECHO OFF
ECHO Cloning android-support-libraries...

git clone https://github.com/dandar3/android-support-annotations.git
cd android-support-annotations
git checkout tags/27.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-support-compat.git
cd android-support-compat
git checkout tags/27.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-support-core-ui.git
cd android-support-core-ui
git checkout tags/27.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-support-core-utils.git
cd android-support-core-utils
git checkout tags/27.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-support-fragment.git
cd android-support-fragment
git checkout tags/27.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-support-media-compat.git
cd android-support-media-compat
git checkout tags/27.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-support-v4.git
cd android-support-v4
git checkout tags/27.1.1
copy ..\local.properties .
cd..
