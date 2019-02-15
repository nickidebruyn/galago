ECHO OFF

ECHO Cloning android-google-play-services-games...

git clone https://github.com/dandar3/android-google-play-services-drive.git google-play-services-drive
cd google-play-services-drive
git checkout tags/15.0.0
copy ..\local.properties .
cd..

git clone https://github.com/dandar3/android-google-play-services-games.git google-play-services-games
cd google-play-services-games
git checkout tags/15.0.0
copy ..\local.properties .
cd..
