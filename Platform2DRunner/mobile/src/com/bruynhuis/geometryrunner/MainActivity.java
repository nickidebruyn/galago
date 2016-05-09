package com.bruynhuis.geometryrunner;
 
import android.content.pm.ActivityInfo;
import com.bruynhuis.galago.android.AbstractGameActivity;
 
public class MainActivity extends AbstractGameActivity {

    @Override
    protected void preload() {
        APP_PATH = "com.bruynhuis.geometryrunner.MainApplication";
 
    }

    @Override
    protected void init() {
        
    }

    @Override
    protected void postLoad() {
    }
 
}
