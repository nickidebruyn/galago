# Galago
A easy to use and complete game development framework build for jMonkeyEngine3.2
It also contains support for android google play services with ant build.


# How to integrate google play services
You have to change your project.properties file to point to the play service folder in galago.

target=android-27
android.library.reference.1=../../../galago/Google/google-play-services-auth
android.library.reference.2=../../../galago/Google/google-play-services-games

You need to install android target API 27.

Include the following activity in your AndroidManifest.xml:
        <activity
            android:name="com.google.android.gms.auth.api.signin.internal.SignInHubActivity"
            android:screenOrientation="portrait"
            android:windowSoftInputMode="stateAlwaysHidden|adjustPan" />
			
Android SDK should be installed under:
sdk.dir=C:/android-sdk