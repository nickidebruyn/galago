/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.example.pinball;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.galago.example.pinball.screens.PlayScreen;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nicki
 */
public class MainApplication extends Base2DApplication {

    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Flipper", 480, 800, "galagopinball.save", null, null, false);
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorRGBA.White;
        frustumSize = 8.2f;
                
    }

    @Override
    protected void postInitApp() {
        showScreen(PlayScreen.NAME);
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen(PlayScreen.NAME, new PlayScreen());

    }

    @Override
    public void initModelManager(ModelManager modelManager) {
    }

    @Override
    protected void initSound(SoundManager soundManager) {
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
    }

    @Override
    protected void initFonts(FontManager fontManager) {
    }

}