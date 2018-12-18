/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.example.hyper2d;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.util.ColorUtils;
import com.galago.example.hyper2d.screens.PlayScreen;

/**
 *
 * @author NideBruyn
 */
public class MainApplication extends Base2DApplication {
    
    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Hyper2DGame", 480, 800, "hyper2d.save", "Interface/Fonts/Roboto.fnt", null, false);
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorUtils.rgb(25, 42, 86);
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
//        modelManager.loadMaterial("Materials/sky.j3m");

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
