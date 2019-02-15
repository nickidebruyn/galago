ECHO OFF

ECHO Cloning android-google-play-services-ads...

git clone https://github.com/dandar3/android-google-play-services-ads.git google-play-services-ads
cd google-play-services-ads
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-ads-base.git google-play-services-ads-base
cd google-play-services-ads-base
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-ads-identifier.git google-play-services-ads-identifier
cd google-play-services-ads-identifier
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-ads-lite.git google-play-services-ads-lite
cd google-play-services-ads-lite
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-gass.git google-play-services-gass
cd google-play-services-gass
git checkout tags/15.0.0
copy ..\local.properties .
cd..