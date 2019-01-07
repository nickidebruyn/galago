/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.example.rocketman;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.util.ColorUtils;
import com.galago.example.rocketman.screens.PlayScreen;

/**
 *
 * @author NideBruyn
 */
public class MainApplication extends Base2DApplication {
    
    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Tiny Rocket Man", 800, 480, "rocketman.save", "Interface/Fonts/Roboto.fnt", null, false);
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorUtils.rgb(5, 5, 5);
        frustumSize = 6.4f;
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
        modelManager.loadMaterial("Materials/ironman.j3m");

    }

    @Override
    protected void initSound(SoundManager soundManager) {
        soundManager.loadMusic("fly", "Sounds/fly.ogg");
        soundManager.setMusicVolume("fly", 0.5f);
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
