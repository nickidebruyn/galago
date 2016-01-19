package com.bruynhuis.galago2d;
 
import com.bruynhuis.galago.android.AbstractGameActivity;
 
public class MainActivity extends AbstractGameActivity {

    @Override
    protected void preload() {
        APP_PATH = "com.bruynhuis.galago.flat.MainApplication";
        PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=com.bruynhuis.galago2d";
        MOREAPPS_URL = "https://play.google.com/store/apps/developer?id=bruynhuis";
        
    }

    @Override
    protected void init() {
        
    }

    @Override
    protected void postLoad() {
    }
 
}
