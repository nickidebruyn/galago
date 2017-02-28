package com.bruynhuis.spaceshooter;
 
import com.bruynhuis.galago.android.AbstractGameActivity;
 
public class MainActivity extends AbstractGameActivity {

    @Override
    protected void preload() {
        APP_PATH = "com.example.spaceshooter.MainApplication";
        PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=com.bruynhuis.spaceshooter";
        MOREAPPS_URL = "https://play.google.com/store/apps/developer?id=bruynhuis";
        
//        usePlayServices = true;
//        
//        useAnalytics = true;
//        ANALYTICS_TRACKER_ID = "UA-64908415-14";
//        
//        useAdmob = true;
//        ADMOB_ID = "ca-app-pub-9553163517721646/5717068618";
//        
//        useAdmobInterstitials = true;
//        ADMOB_INTERSTITIALS_ID = "ca-app-pub-9553163517721646/9603725814";          
//        
//        splashPicID = R.drawable.splash;
    }

    @Override
    protected void init() {
//        eglConfigType = AndroidConfigChooser.ConfigType.FASTEST;
    }

    @Override
    protected void postLoad() {

    }

}
