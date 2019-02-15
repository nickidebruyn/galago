ECHO OFF

ECHO Cloning android-google-play-services-auth...

git clone https://github.com/dandar3/android-google-play-services-auth.git google-play-services-auth
cd google-play-services-auth
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-auth-api-phone.git google-play-services-auth-api-phone
cd google-play-services-auth-api-phone
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-auth-base.git google-play-services-auth-base
cd google-play-services-auth-base
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-base.git google-play-services-base
cd google-play-services-base
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-basement.git google-play-services-basement
cd google-play-services-basement
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-tasks.git google-play-services-tasks
cd google-play-services-tasks
git checkout tags/15.0.0
copy ..\local.properties .
cd..