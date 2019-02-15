ECHO OFF
ECHO Cloning android-arch...
git clone https://github.com/dandar3/android-arch-core-common.git
cd android-arch-core-common
git checkout tags/1.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-arch-core-runtime.git
cd android-arch-core-runtime
git checkout tags/1.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-arch-lifecycle-common.git
cd android-arch-lifecycle-common
git checkout tags/1.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-arch-lifecycle-livedata-core.git
cd android-arch-lifecycle-livedata-core
git checkout tags/1.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-arch-lifecycle-runtime.git
cd android-arch-lifecycle-runtime
git checkout tags/1.1.1
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-arch-lifecycle-viewmodel.git
cd android-arch-lifecycle-viewmodel
git checkout tags/1.1.1
copy ..\local.properties .
cd..