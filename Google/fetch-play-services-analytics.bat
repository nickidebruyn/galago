ECHO OFF

ECHO Cloning android-google-play-services-analytics...

git clone https://github.com/dandar3/android-google-play-services-ads-identifier.git google-play-services-ads-identifier
cd google-play-services-ads-identifier
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-analytics.git google-play-services-analytics
cd google-play-services-analytics
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-analytics-impl.git google-play-services-analytics-impl
cd google-play-services-analytics-impl
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-measurement-base.git google-play-services-measurement-base
cd google-play-services-measurement-base
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-stats.git google-play-services-stats
cd google-play-services-stats
git checkout tags/15.0.0
copy ..\local.properties .
cd..
