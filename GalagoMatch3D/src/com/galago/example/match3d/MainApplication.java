/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.example.match3d;

import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.util.ColorUtils;
import com.galago.example.match3d.screens.PlayScreen;
import com.galago.example.match3d.screens.PlayScreenChaseCam;

/**
 *
 * @author NideBruyn
 */
public class MainApplication extends Base3DApplication {
    
    public static void main(String[] args) {
        new MainApplication();
    }

    public MainApplication() {
        super("Color Cube", 480, 800, "match3d.save", "Interface/Fonts/Roboto.fnt", "Interface/splash.jpg", false);
    }

    @Override
    protected void preInitApp() {
        BACKGROUND_COLOR = ColorUtils.hsv(0.9f, 0.5f, .9f);
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
    protected void initCamera() {
        super.initCamera();
//        setOrthographicProjection(6);

    }
    
    @Override
    protected void initScreens(ScreenManager screenManager) {
//        screenManager.loadScreen(PlayScreen.NAME, new PlayScreen());
        screenManager.loadScreen(PlayScreenChaseCam.NAME, new PlayScreenChaseCam());

    }

    @Override
    public void initModelManager(ModelManager modelManager) {
//        modelManager.loadMaterial("Materials/sky.j3m");

    }

    @Override
    protected void initSound(SoundManager soundManager) {
        soundManager.loadSoundFx("button", "Sounds/button.ogg");
        soundManager.loadSoundFx("drop", "Sounds/drop3.ogg");
        soundManager.loadSoundFx("place", "Sounds/place.ogg");
        soundManager.loadSoundFx("pop", "Sounds/pop.ogg");
        soundManager.loadSoundFx("booster", "Sounds/twinkle.ogg");
        soundManager.loadSoundFx("levelup", "Sounds/levelup.ogg");
        soundManager.loadSoundFx("gameover", "Sounds/gameover.ogg");
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        effectManager.loadEffect("cube-destroy", "Models/Effects/cube-destroy.j3o");
        effectManager.loadEffect("cube-place", "Models/Effects/cube-place.j3o");
        effectManager.loadEffect("level-up", "Models/Effects/level-up.j3o");

    }

    @Override
    protected void initTextures(TextureManager textureManager) {
    }

    @Override
    protected void initFonts(FontManager fontManager) {
        fontManager.loadFont(new FontStyle(62, 4));
        fontManager.loadFont(new FontStyle(30));
        fontManager.loadFont(new FontStyle(54));
    }

}
