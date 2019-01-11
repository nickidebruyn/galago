package com.bruynhuis.colorcube;
 
import com.bruynhuis.galago.android.AbstractGameActivity;
 
public class MainActivity extends AbstractGameActivity {

    @Override
    protected void preload() {
        APP_PATH = "com.galago.example.match3d.MainApplication";
        PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=com.bruynshuis.colorcube";
        MOREAPPS_URL = "https://play.google.com/store/apps/developer?id=bruynhuis";
        
    }

    @Override
    protected void init() {
        
    }

    @Override
    protected void postLoad() {
    }

}
