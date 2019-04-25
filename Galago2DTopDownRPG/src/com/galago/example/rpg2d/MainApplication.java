/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.example.rpg2d;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.galago.example.rpg2d.screens.PlayScreen;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class MainApplication extends Base2DApplication {

    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("RPG 2D Example", 1280, 800, "rpg2d.save", null, null, false);
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorRGBA.White;
    }

    @Override
    protected void postInitApp() {
        showScreen(PlayScreen.NAME);
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return false;
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
