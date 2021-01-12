/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.test2d;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.galago.test2d.screens.MenuScreen;
import com.galago.test2d.spritebatching.BatchingScreen;

/**
 *
 * @author NideBruyn
 */
public class MainApplication extends Base2DApplication {

    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Galago 2D Test", 1600, 900, "galago2dtests.save", null, null, false);
    }

    @Override
    protected void preInitApp() {
    }

    @Override
    protected void postInitApp() {
        showScreen(MenuScreen.NAME);
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return false;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen(MenuScreen.NAME, new MenuScreen());
        screenManager.loadScreen(BatchingScreen.NAME, new BatchingScreen());

    }

    @Override
    public void initModelManager(ModelManager modelManager) {
        modelManager.loadMaterial("Materials/tileset.j3m");
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
